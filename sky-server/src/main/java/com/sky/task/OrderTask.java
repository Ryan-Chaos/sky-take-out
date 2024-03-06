package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ")//每分钟触发一次
    public void processTimeOutOrder(){
        log.info("定时处理超时订单，当前时间：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);

        List<Orders> orders = orderMapper.getByStatusAndLTOrderTime(Orders.PENDING_PAYMENT,time);

        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                //取消订单
                Orders newOrder = new Orders();
                newOrder.setId(order.getId());
                newOrder.setStatus(Orders.CANCELLED);
                newOrder.setCancelReason("订单超时未支付，自动取消");
                newOrder.setCancelTime(LocalDateTime.now());

                orderMapper.update(newOrder);
            }
        }
    }


    /**
     * 处理派送中订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")//每天凌晨一点执行
//    @Scheduled(cron = "0/5 * * * * ? ")
    public void processDeliveryOrder(){
        log.info("定时处理派送未完成订单，当前时间：{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);

        List<Orders> orders = orderMapper.getByStatusAndLTOrderTime(Orders.DELIVERY_IN_PROGRESS,time);

        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                //完成订单
                Orders newOrder = new Orders();
                newOrder.setId(order.getId());
                newOrder.setStatus(Orders.COMPLETED);
                newOrder.setDeliveryTime(LocalDateTime.now());

                orderMapper.update(newOrder);
            }
        }


    }


}
