package com.increff.employee.controller;

import com.increff.employee.dto.BrandDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/brands")
public class BrandApiController {


    @Autowired
    private BrandDto dto;

    @ApiOperation(value = "Adds a brand")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void addBrand(@RequestBody BrandForm form) throws ApiException {
        dto.addBrand(form);
    }


    @ApiOperation(value = "Gets list of all brands")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<BrandData> getAllBrand() {

        return dto.getAllBrand();
    }


    @ApiOperation(value = "Gets a brand by ID")
    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable int id) throws ApiException {
        BrandData p = dto.getBrandById(id);
        return (p);
    }


    @ApiOperation(value = "Edit a Brand")
    @RequestMapping(path = "{id}", method = RequestMethod.PUT)
    public void editBrand(@PathVariable int id, @RequestBody BrandForm form) throws ApiException {
        dto.updateBrand(id, form);
    }

//
//    @Scheduled(cron = "0 0 0 ? * * *")
//    private void cron() {
//
//    }

}
