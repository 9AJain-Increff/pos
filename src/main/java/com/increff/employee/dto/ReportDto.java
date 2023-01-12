package com.increff.employee.dto;

import com.increff.employee.model.*;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDto {

    @Autowired
    private ReportService reportService;
    public List<SalesData> getSalesReport(SalesForm form) throws ApiException {

        List<SalesData> salesData = reportService.getSalesReport(form.getBrandName(), form.getBrandCategory(),
                form.getStartTime(), form.getEndTime());
        return salesData;
    }
    public List<InventoryReportData> getInventoryReport() throws ApiException {

        List<InventoryReportData> inventoryReportData = reportService.getInventoryReport();
        return inventoryReportData;
    }

    public List<BrandData> getBrandReport() throws ApiException {

        List<BrandData> brands = reportService.getBrandReport();
        return brands;
    }

    public List<DailyData> getDailyReport() throws ApiException {

        List<DailyData> dailyReports = reportService.getDailyReport();
        return dailyReports;
    }


}
