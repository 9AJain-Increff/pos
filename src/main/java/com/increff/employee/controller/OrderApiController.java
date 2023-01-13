package com.increff.employee.controller;

import com.increff.employee.dto.OrderDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/orders")
public class OrderApiController {


    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Adds a order")
    // todo change path
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void addOrder(@RequestBody List<OrderItemForm> form) throws ApiException {
        dto.addOrder(form);
    }

    @ApiOperation(value = "Deletes a order")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void deleteOrder(@PathVariable int id) throws ApiException {
        dto.deleting(id);
    }

    @ApiOperation(value = "Gets list of all orders")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<OrderData> getAllOrder() {
        return dto.gettingAllOrder();
    }


    @ApiOperation(value = "Gets a order by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable int id) throws ApiException {
        return dto.getOrderById(id);
    }


    @ApiOperation(value = "Edit a Order")
    // todo replace with logger
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void editOrder(@PathVariable int id, @RequestBody List<OrderItemForm> form) throws ApiException {
        dto.updating(id, form);

    }

}
