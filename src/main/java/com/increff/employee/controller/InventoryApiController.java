package com.increff.employee.controller;

import com.increff.employee.dto.InventoryDto;
import com.increff.employee.dto.ProductDto;
import com.increff.employee.model.*;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.EmployeePojo;
import com.increff.employee.pojo.UserPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Api
@RestController
public class InventoryApiController {


    @Autowired
    private InventoryDto dto;

    @ApiOperation(value = "Adds a inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.POST)
    public void addInventory(@RequestBody InventoryForm form) throws ApiException {
        dto.addingInventory(form);
    }

    @ApiOperation(value = "Deletes a inventory")
    @RequestMapping(path = "/api/inventory/{barcode}", method = RequestMethod.DELETE)
    public void deleteInventory(@PathVariable String barcode) throws ApiException {
        dto.deleting(barcode);
    }

    @ApiOperation(value = "Gets list of all inventorys")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
    public List<InventoryData> getAllInventory() {
        System.out.println("anknanana");
        return dto.gettingAllInventory();
    }


    @ApiOperation(value = "Gets a inventory by barode")
    @RequestMapping(path = "/api/inventory/{barcode}", method = RequestMethod.GET)
    public InventoryData get(@PathVariable String barcode) throws ApiException {
        InventoryData p = dto.get(barcode);
        return (p);
    }



    @ApiOperation(value = "Edit a Inventory")
    @RequestMapping(path = "/api/inventory/{barcode}", method = RequestMethod.PUT)
    public void editInventory(@PathVariable String barcode, @RequestBody InventoryForm form) throws ApiException {
        dto.updating(form);
    }

}
