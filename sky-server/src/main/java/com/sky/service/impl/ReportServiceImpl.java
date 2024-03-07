package com.sky.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnover(LocalDate begin, LocalDate end) {
        List<LocalDate> datelist = new ArrayList<>();
        datelist.add(begin);

        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            datelist.add(begin);
        }

        String dateJoin = StringUtils.join(datelist, ",");

        List<BigDecimal> turnoverList = new ArrayList<>();

        for (LocalDate date : datelist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            BigDecimal turnover = orderMapper.getByTime(beginTime,endTime, Orders.COMPLETED);
            if(turnover == null){
                turnover = BigDecimal.valueOf(0);
            }

            turnoverList.add(turnover);
        }

        String turnoverJoin = StringUtils.join(turnoverList,",");

        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder().dateList(dateJoin).turnoverList(turnoverJoin).build();

        return turnoverReportVO;
    }


    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userReport(LocalDate begin, LocalDate end) {
        List<LocalDate> datelist = new ArrayList<>();
        datelist.add(begin);

        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            datelist.add(begin);
        }

        String dateJoin = StringUtils.join(datelist, ",");

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : datelist) {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            Integer newUserNumber = userMapper.getNumber(beginTime,endTime);
            if(newUserNumber == null)newUserNumber=0;
            newUserList.add(newUserNumber);

            Integer totalUserNumber = userMapper.getNumber(null,endTime);
            if(totalUserNumber==null)totalUserNumber=0;
            totalUserList.add(totalUserNumber);
        }
        String newUser = StringUtils.join(newUserList,",");
        String totalUser = StringUtils.join(totalUserList,",");

        UserReportVO userReportVO = UserReportVO.builder().
                dateList(dateJoin).newUserList(newUser).totalUserList(totalUser).build();

        return userReportVO;
    }


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datelist = new ArrayList<>();
        datelist.add(begin);
        LocalDate firstBegin = begin;

        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            datelist.add(begin);
        }

        String dateJoin = StringUtils.join(datelist, ",");


        //订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        //有效订单列表
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : datelist) {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            //不限制状态，查询当日总订单数
            Integer orderCount = orderMapper.getNumberByTime(beginTime,endTime,null);
            orderCountList.add(orderCount);
            //已完成状态，查询当时有效订单数
            Integer validCount = orderMapper.getNumberByTime(beginTime,endTime,Orders.COMPLETED);
            validOrderCountList.add(validCount);
        }
        String orderCoungListString = StringUtils.join(orderCountList,",");
        String validOrderCoungListString = StringUtils.join(validOrderCountList,",");

        LocalDateTime beginTime = LocalDateTime.of(firstBegin,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

        //订单总数
        int totalOrderCount = orderMapper.getNumberByTime(beginTime,endTime,null);
        //有效订单数
        int validOrderCount = orderMapper.getNumberByTime(beginTime,endTime,Orders.COMPLETED);
        //订单完成率
        double orderCompletionRate = 0.0;
        if(totalOrderCount!=0){
            orderCompletionRate = (double) validOrderCount /totalOrderCount;
        }

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(dateJoin)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(orderCoungListString)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .validOrderCountList(validOrderCoungListString)
                .build();

        return orderReportVO;
    }


    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOS = orderMapper.getNameAndNumber(beginTime,endTime);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for (GoodsSalesDTO goodSales : goodsSalesDTOS) {
            nameList.add(goodSales.getName());
            numberList.add(goodSales.getNumber());
        }
        String nameListString = StringUtils.join(nameList,",");
        String numberListString = StringUtils.join(numberList,",");

        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameListString).numberList(numberListString).build();
        return salesTop10ReportVO;
    }
}
