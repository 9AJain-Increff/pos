package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/products")
public class ProductApiController {
    @Autowired
    private ProductDto productDto;

    @ApiOperation(value = "Adds a product")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public ProductData addProduct(@RequestBody ProductForm form) throws ApiException {
        return productDto.addProduct(form);
    }

    @ApiOperation(value = "Gets list of all products")
    @RequestMapping(path = "", method = RequestMethod.GET)
    // TODO: 29/01/23 why getAll is throwing ApiException?
    public List<ProductData> getAllProduct() throws ApiException {
        return productDto.getAllProduct();
    }

    @ApiOperation(value = "Gets a product by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable Integer id) throws ApiException {
        return (productDto.getProductById(id));
    }

    @ApiOperation(value = "Edit a Product")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ProductData updateProduct(@PathVariable Integer id, @RequestBody ProductForm form) throws ApiException {
            return productDto.update(id, form);
    }
}
