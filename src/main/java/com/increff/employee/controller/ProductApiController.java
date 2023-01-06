package com.increff.employee.controller;

import com.increff.employee.dto.ProductDto;
import com.increff.employee.dto.ProductDto;
import com.increff.employee.model.*;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.pojo.EmployeePojo;
import com.increff.employee.pojo.UserPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.ProductService;
import com.increff.employee.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Api
@RestController
public class ProductApiController {


    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "Adds a product")
    @RequestMapping(path = "/api/product", method = RequestMethod.POST)
    public void addProduct(@RequestBody ProductForm form) throws ApiException {
        System.out.println("ankur jainnnnnnnnnnn");
        dto.addingProduct(form);
    }

    @ApiOperation(value = "Deletes a product")
    @RequestMapping(path = "/api/product/{barcode}", method = RequestMethod.DELETE)
    public void deleteProduct(@PathVariable String barcode) throws ApiException {
        dto.deleting(barcode);
    }

    @ApiOperation(value = "Gets list of all products")
    @RequestMapping(path = "/api/product", method = RequestMethod.GET)
    public List<ProductData> getAllProduct() {

        return dto.gettingAllProduct();
    }


    @ApiOperation(value = "Gets a product by ID")
    @RequestMapping(path = "/api/product/{barcode}", method = RequestMethod.GET)
    public ProductData get(@PathVariable String barcode) throws ApiException {
        System.out.println("get by id");
        ProductData p = dto.get(barcode);
        System.out.println("ankur jainiiiiiiiiiinini      "+p.getBarcode());
        return (p);
    }



    @ApiOperation(value = "Edit a Product")
    @RequestMapping(path = "/api/product/{barcode}", method = RequestMethod.PUT)
    public void editProduct(@PathVariable String barcode, @RequestBody ProductForm form) throws ApiException {
        dto.updating(barcode,form);

    }

}
