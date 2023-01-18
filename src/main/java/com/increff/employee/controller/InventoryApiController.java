package com.increff.employee.controller;

import com.increff.employee.dto.InventoryDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api
@RestController
@RequestMapping(path = "/api/inventory")
public class InventoryApiController {


    @Autowired
    private InventoryDto inventoryDto;

    @ApiOperation(value = "Adds a inventory")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void addInventory(@RequestBody InventoryForm form) throws ApiException {
        inventoryDto.addInventory(form);
    }

//    @ApiOperation(value = "Deletes a inventory")
//    @RequestMapping(path = "/{barcode}", method = RequestMethod.DELETE)
//    public void deleteInventory(@PathVariable String barcode) throws ApiException {
//        inventoryDto.deleting(barcode);
//    }

    @ApiOperation(value = "Gets list of all inventory")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<InventoryData> getAllInventory() throws ApiException {
        return inventoryDto.getAllInventory();
    }


    @ApiOperation(value = "Gets a inventory by barode")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.GET)
    public InventoryData get(@PathVariable String barcode) throws ApiException {
        InventoryData p = inventoryDto.getInventoryByBarcode(barcode);
        return (p);
    }



    @ApiOperation(value = "Edit a Inventory")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.PUT)
    public void editInventory(@PathVariable String barcode, @RequestBody InventoryForm form) throws ApiException {
        inventoryDto.updateInventory(form);
    }

}
