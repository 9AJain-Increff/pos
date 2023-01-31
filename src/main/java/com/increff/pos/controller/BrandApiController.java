package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    public BrandData addBrand(@RequestBody BrandForm form) throws ApiException {
        return dto.addBrand(form);
    }

    @ApiOperation(value = "Gets list of all brands")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<BrandData> getAllBrand() {
        return dto.getAllBrand();
    }

    @ApiOperation(value = "Gets a brand by ID")
    @RequestMapping(path = "{orderId}", method = RequestMethod.GET)
    public BrandData get(@PathVariable int orderId) throws ApiException {
        BrandData p = dto.getBrandById(orderId);
        return (p);
    }

    @ApiOperation(value = "Edit a Brand")
    @RequestMapping(path = "{id}", method = RequestMethod.PUT)
    public BrandData updateBrand(@PathVariable int id, @RequestBody BrandForm form) throws ApiException {
        return dto.updateBrand(id, form);
    }


}
