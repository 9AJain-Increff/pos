package com.increff.pos.controller;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailyData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.DailyReportForm;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
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
    @RequestMapping(path = "/inventory", method = RequestMethod.POST)
    public List<InventoryReportData> getInventoryReport(@RequestBody BrandForm form) throws ApiException {
        return reportDto.getInventoryReport(form);
    }

    @ApiOperation(value = "Get Brand report")
    @RequestMapping(path = "/brand", method = RequestMethod.POST)
    public List<BrandData> getBrandReport(@RequestBody BrandForm form) throws ApiException {
        return reportDto.getBrandReport(form);
    }

    @Scheduled(cron = "0 0 0 ? * *")
    public void updatePerDaySale() {
        reportDto.updatePerDaySale();
    }

    @ApiOperation(value = "Get day to day report")
    @RequestMapping(path = "/daily", method = RequestMethod.POST)
    public List<DailyData> getDailyReport(@RequestBody DailyReportForm form) throws ApiException {
        return reportDto.getDailyReport(form);
    }

}
