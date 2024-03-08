package com.sky.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 导出Excel报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate beginDay = LocalDate.now().minusDays(30);
        LocalDateTime begin = LocalDateTime.of(beginDay,LocalTime.MIN);

        LocalDate endDay = LocalDate.now().minusDays(1);
        LocalDateTime end = LocalDateTime.of(endDay,LocalTime.MAX);
        //查询数据库，获取运营数据
        BusinessDataVO businessData = workspaceService.getBusinessData(begin, end);

        //通过POI写入excel文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet = excel.getSheetAt(0);
            //填充数据
            //时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + beginDay.toString() + "到" + endDay.toString());
            //概览数据
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());

            //明细数据
            for(int i=0;i<30;i++){
                //获取日期
                LocalDate day = beginDay.plusDays(i);
                //获取时间
                LocalDateTime beginTime = LocalDateTime.of(day,LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(day,LocalTime.MAX);
                //获得运营数据
                BusinessDataVO business = workspaceService.getBusinessData(beginTime, endTime);
                //填充数据
                sheet.getRow(7+i).getCell(1).setCellValue(day.toString());
                sheet.getRow(7+i).getCell(2).setCellValue(business.getTurnover());
                sheet.getRow(7+i).getCell(3).setCellValue(business.getValidOrderCount());
                sheet.getRow(7+i).getCell(4).setCellValue(business.getOrderCompletionRate());
                sheet.getRow(7+i).getCell(5).setCellValue(business.getUnitPrice());
                sheet.getRow(7+i).getCell(6).setCellValue(business.getNewUsers());
            }


            //通过输出流将文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
