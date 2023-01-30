package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.service.ApiException;
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
    public BrandData.InventoryData addInventory(@RequestBody InventoryForm form) throws ApiException {
        return inventoryDto.addInventory(form);
    }


    @ApiOperation(value = "Gets list of all inventory")
    @RequestMapping(path = "", method = RequestMethod.GET)
    // TODO: 29/01/23 why getAll is throwing ApiException?
    public List<BrandData.InventoryData> getAllInventory() throws ApiException {
        return inventoryDto.getAllInventory();
    }


    // TODO: 29/01/23 use id instead of barcode in the path
    @ApiOperation(value = "Gets a inventory by barode")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.GET)
    public BrandData.InventoryData get(@PathVariable String barcode) throws ApiException {
        BrandData.InventoryData p = inventoryDto.getInventoryByBarcode(barcode);
        return (p);
    }


    // TODO: 29/01/23 use id instead of barcode in the path
    @ApiOperation(value = "Edit a Inventory")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.PUT)
    public BrandData.InventoryData updateInventory(@PathVariable String barcode, @RequestBody InventoryForm form) throws ApiException {
        return inventoryDto.updateInventory(form);
    }

}
