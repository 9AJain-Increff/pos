package com.increff.pos.controller;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailyData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
// FIXED: 29/01/23 where is the scheduler to populate daily data?
@RestController
@RequestMapping(path = "/api/reports")
public class ReportApiController {


    @Autowired
    private ReportDto reportDto;

    @ApiOperation(value = "Get Sales report")
    @RequestMapping(path = "/sales", method = RequestMethod.POST)
    public List<SalesData> getSalesReport(@RequestBody SalesForm form) throws ApiException {
        return reportDto.getSalesReport(form);
    }

    @ApiOperation(value = "Get inventory report")
    @RequestMapping(path = "/inventory", method = RequestMethod.GET)
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        return reportDto.getInventoryReport();
    }

    @ApiOperation(value = "Get Brand report")
    @RequestMapping(path = "/brand", method = RequestMethod.GET)
    public List<BrandData> getBrandReport() throws ApiException {
        return reportDto.getBrandReport();
    }

    @Scheduled(cron = "0 0 0 ? * *")
//    @Scheduled(fixedDelay = 10000)
    public void updatePerDaySale() {
        reportDto.updatePerDaySale();
    }

    @ApiOperation(value = "Get day to day report")

    @RequestMapping(path = "/daily", method = RequestMethod.GET)
    public List<DailyData> getDailyReport() throws ApiException {
        return reportDto.getDailyReport();
    }

}
