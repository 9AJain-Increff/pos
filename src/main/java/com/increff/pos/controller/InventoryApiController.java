package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.exception.ApiException;
import com.sun.xml.bind.v2.model.core.ID;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/inventory")
public class InventoryApiController {


    @Autowired
    private InventoryDto inventoryDto;

    @ApiOperation(value = "Adds a inventory")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public InventoryData addInventory(@RequestBody InventoryForm form) throws ApiException {
        return inventoryDto.addInventory(form);
    }


    @ApiOperation(value = "Gets list of all inventory")
    @RequestMapping(path = "", method = RequestMethod.GET)
//    public List<InventoryData> getAllInventory() throws ApiException {
//        return inventoryDto.getAllInventory();
//    }
    public List<InventoryData> getAllInventory(@RequestParam(required = false) String barcode) throws ApiException {
        if (barcode != null) {
                return Collections.singletonList(inventoryDto.getInventoryByBarcode(barcode));
        } else {
            return inventoryDto.getAllInventory();
        }
    }


    @ApiOperation(value = "Gets a inventory by product id")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public InventoryData get(@PathVariable Integer id) throws ApiException {
        InventoryData p = inventoryDto.getInventoryByProductId(id);
        return (p);
    }


    // TODO: 29/01/23 use id instead of barcode in the path
    @ApiOperation(value = "Edit a Inventory")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.PUT)
    public InventoryData updateInventory(@PathVariable String barcode, @RequestBody InventoryForm form) throws ApiException {
        return inventoryDto.updateInventory(form);
    }

}
