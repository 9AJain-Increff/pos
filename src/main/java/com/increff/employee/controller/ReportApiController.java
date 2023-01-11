package com.increff.employee.controller;

import com.increff.employee.dto.ReportDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
public class ReportApiController {


    @Autowired
    private ReportDto reportDto ;

    @ApiOperation(value = "Adds a order")
    // todo change path
    @RequestMapping(path = "/api/report", method = RequestMethod.POST)
    public void getReport(@RequestBody SalesForm form) throws ApiException {
        reportDto.getSales(form);
    }


}
