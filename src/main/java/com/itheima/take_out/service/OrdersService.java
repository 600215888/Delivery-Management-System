package com.itheima.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.take_out.entity.Orders;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
