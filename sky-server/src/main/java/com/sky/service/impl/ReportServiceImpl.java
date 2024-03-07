package com.sky.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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
}
