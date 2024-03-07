package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getByOrderId(Long id);

    /**
     * 查找各个状态的订单数量
     * @param status
     * @return
     */
    @Select("select count(0) from orders where status = #{status}")
    Integer getNumber(Integer status);

    /**
     * 根据状态和下单时间查找订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndLTOrderTime(Integer status, LocalDateTime orderTime);


    /**
     * 获取某天的营业额
     * @param begin
     * @param end
     * @param status
     * @return
     */

    BigDecimal getByTime(LocalDateTime begin,LocalDateTime end,Integer status);


    /**
     * 查询某天的订单数
     * @param begin
     * @param end
     * @param status
     * @return
     */
    Integer getNumberByTime(LocalDateTime begin,LocalDateTime end,Integer status);


    /**
     * 查询销量排名top10的name和number
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getNameAndNumber(LocalDateTime beginTime, LocalDateTime endTime);
}
