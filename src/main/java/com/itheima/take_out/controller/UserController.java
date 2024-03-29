package com.itheima.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.take_out.common.R;
import com.itheima.take_out.entity.User;
import com.itheima.take_out.service.UserService;
import com.itheima.take_out.utils.SMSUtils;
import com.itheima.take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code: {}",code);
            //session.setAttribute(phone, code);
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("短信验证码发送成功");
        }
        return R.error("短信发送失败");

    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //Object codeInSession = session.getAttribute(phone);
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        if(codeInSession != null && codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");

    }
}
