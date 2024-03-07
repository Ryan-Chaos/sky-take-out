package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {


    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnover(LocalDate begin, LocalDate end);


    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userReport(LocalDate begin, LocalDate end);


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO orderStatistics(LocalDate begin, LocalDate end);
}
