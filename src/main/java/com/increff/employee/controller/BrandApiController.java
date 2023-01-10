package com.increff.employee.controller;

import com.increff.employee.dto.BrandDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class BrandApiController {


    @Autowired
    private BrandDto dto;

    @ApiOperation(value = "Adds a brand")
    @RequestMapping(path = "/api/brand", method = RequestMethod.POST)
    public void addBrand(@RequestBody BrandForm form) throws ApiException {
        dto.addBrand(form);
    }

    @ApiOperation(value = "Deletes a brand")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.DELETE)
    public void deleteBrand(@PathVariable int id) throws ApiException {
        dto.deleteBrand(id);
    }

    @ApiOperation(value = "Gets list of all brands")
    @RequestMapping(path = "/api/brand", method = RequestMethod.GET)
    public List<BrandData> getAllBrand() {

        return dto.getAllBrand();
    }


    @ApiOperation(value = "Gets a brand by ID")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable int id) throws ApiException {
        BrandData p = dto.getBrandById(id);
        return (p);
    }


    @ApiOperation(value = "Edit a Brand")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
    public void editBrand(@PathVariable int id, @RequestBody BrandForm form) throws ApiException {
        dto.updateBrand(id, form);
    }

}
