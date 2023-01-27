package com.increff.pos.controller;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.*;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class    ReportApiController {


    @Autowired
    private ReportDto reportDto ;

    @ApiOperation(value = "Get Sales report")
    // todo change path
    @RequestMapping(path = "/api/report/sales"  , method = RequestMethod.POST)
    public List<SalesData> getSalesReport(@RequestBody SalesForm form) throws ApiException {
        return reportDto.getSalesReport(form);
    }
    @ApiOperation(value = "Get inventory report")
    // todo change path
    @RequestMapping(path = "/api/report/inventory", method = RequestMethod.GET)
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        return reportDto.getInventoryReport();
    }

    @ApiOperation(value = "Get Brand report")
    // todo change path
    @RequestMapping(path = "/api/report/brand", method = RequestMethod.GET)
    public List<BrandData> getBrandReport() throws ApiException {
        return reportDto.getBrandReport();
    }
    @ApiOperation(value = "Get day to day report")
    // todo change path
    @RequestMapping(path = "/api/report/daily", method = RequestMethod.GET)
    public List<DailyData> getDailyReport() throws ApiException {
        return reportDto.getDailyReport();
    }

}
