package com.itheima.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.take_out.dto.SetmealDto;
import com.itheima.take_out.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
