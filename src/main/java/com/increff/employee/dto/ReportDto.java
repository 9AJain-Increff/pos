package com.increff.employee.dto;

import com.increff.employee.model.SalesData;
import com.increff.employee.model.SalesForm;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDto {

    @Autowired
    private ReportService reportService;
    public List<SalesData> getSales(SalesForm form) throws ApiException {

        List<SalesData> salesData = reportService.getSalesReport(form.getBrandCategory(), form.getBrandName(),
                form.getStartTime(), form.getEndTime());
        return salesData;
    }
}
