package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.*;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
// TODO: 29/01/23 every add/edit method should return corresponding data
// TODO: 29/01/23 format the lines properly
@RequestMapping(path = "/api/products")
public class ProductApiController {
    @Autowired
    private ProductDto productDto;

    @ApiOperation(value = "Adds a product")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public ProductPojo addProduct(@RequestBody ProductForm form) throws ApiException {
        return productDto.addProduct(form);
    }

    @ApiOperation(value = "Gets list of all products")
    @RequestMapping(path = "", method = RequestMethod.GET)
    // TODO: 29/01/23 why getAll is throwing ApiException?
    public List<ProductData> getAllProduct() throws ApiException {
        return productDto.getAllProduct();
    }

    @ApiOperation(value = "Gets a product by ID")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.GET)
    // TODO: 29/01/23 use id instead of barcode
    public ProductData get(@PathVariable String barcode) throws ApiException {
        ProductData p = productDto.getProductByBarcode(barcode);
        return (p);
    }

    @ApiOperation(value = "Edit a Product")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void editProduct(@PathVariable Integer id, @RequestBody ProductForm form) throws ApiException {
        productDto.update(id, form);

    }
}
