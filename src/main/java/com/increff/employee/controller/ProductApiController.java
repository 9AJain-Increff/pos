package com.increff.employee.controller;

import com.increff.employee.dto.ProductDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api
@RestController
public class ProductApiController {


    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "Adds a product")
    @RequestMapping(path = "/api/product", method = RequestMethod.POST)
    public void addProduct(@RequestBody ProductForm form) throws ApiException {

        dto.addProduct(form);
    }

    @ApiOperation(value = "Deletes a product")
    @RequestMapping(path = "/api/product/{barcode}", method = RequestMethod.DELETE)
    public void deleteProduct(@PathVariable String barcode) throws ApiException {
        dto.delete(barcode);
    }

    @ApiOperation(value = "Gets list of all products")
    @RequestMapping(path = "/api/product", method = RequestMethod.GET)
    public List<ProductData> getAllProduct() {

        return dto.getAllProduct();
    }


    @ApiOperation(value = "Gets a product by ID")
    @RequestMapping(path = "/api/product/{barcode}", method = RequestMethod.GET)
    public ProductData get(@PathVariable String barcode) throws ApiException {
        ProductData p = dto.getProductByBarcode(barcode);
        return (p);
    }



    @ApiOperation(value = "Edit a Product")
    @RequestMapping(path = "/api/product/{barcode}", method = RequestMethod.PUT)
    public void editProduct(@PathVariable String barcode, @RequestBody ProductForm form) throws ApiException {
        dto.update(barcode,form);

    }

}
