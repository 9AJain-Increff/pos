package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Api
@RestController
// TODO: 29/01/23 every add/edit method should return corresponding data....what to return in update order?
@RequestMapping(path = "/api/orders")
public class OrderApiController {
    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds a order")
    // FIXED make it orderData
    @RequestMapping(path = "", method = RequestMethod.POST)
    public OrderData addOrder(@RequestBody List<OrderItemForm> form) throws ApiException {
        return orderDto.addOrder(form);
    }

    @ApiOperation(value = "Get a Pdf Url")
    // todo change path
    @RequestMapping(path = "/invoice/{id}", method = RequestMethod.GET)
    public void getPdf(@PathVariable Integer id, HttpServletResponse response) throws ApiException {
        String filePath = orderDto.getOrderPdf(id);
        response.setContentType("application/pdf");
        response.addHeader("Content-disposition:", "attachment; filename=invoice-" + id);

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            IOUtils.copy(fileInputStream, response.getOutputStream());
            fileInputStream.close();
            response.flushBuffer();
        } catch (IOException e) {
            throw new ApiException("Error occured while downloading invoice!");
        }
    }

    @ApiOperation(value = "Gets list of all orders")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<OrderData> getAllOrder() {
        return orderDto.getAllOrders();
    }

    @ApiOperation(value = "Gets a order by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable int id) throws ApiException {
        return orderDto.getOrderItemsById(id);
    }

    @ApiOperation(value = "Edit a Order")
    // FIXED: 29/01/23 move pdf gen to dto
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void updateOrder(@PathVariable int id, @RequestBody List<OrderItemForm> form) throws ApiException {
        orderDto.updateOrder(id, form);

    }


    // FIXED: 29/01/23 replace system.out with logger

}
