package com.increff.employee.dto;

import com.increff.employee.model.*;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.DailyReportPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.increff.employee.util.ConversionUtil.convertToDailyData;

@Component
public class ReportDto {

    @Autowired
    private ReportService reportService;
    public List<SalesData> getSalesReport(SalesForm form) throws ApiException {


        List<SalesData> salesReport = reportService.getSalesReport(form.getBrandName(), form.getBrandCategory(),
                form.getStartTime(), form.getEndTime());
        return salesReport;
    }
    public List<InventoryReportData> getInventoryReport() throws ApiException {

        return reportService.getInventoryReport();
    }

    public List<BrandData> getBrandReport() throws ApiException {

        List<BrandData> brands = reportService.getBrandReport();
        return brands;
    }

    public List<DailyData> getDailyReport() throws ApiException {

        List<DailyReportPojo> dailyReports = reportService.getDailyReport();
        List<DailyData> dailyData = new ArrayList<>();
        for(DailyReportPojo d: dailyReports){
            dailyData.add(convertToDailyData(d));
        }
        return dailyData;
    }


}
