package com.increff.employee.controller;

import com.increff.employee.dto.OrderDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/orders")
public class OrderApiController {


    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds a order")
    // todo change path
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void addOrder(@RequestBody List<OrderItemForm> form) throws ApiException {
        orderDto.addOrder(form);
    }

//    @ApiOperation(value = "Deletes a order")
//    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//    public void deleteOrder(@PathVariable int id) throws ApiException {
//        orderDto.deleting(id);
//    }

    @ApiOperation(value = "Gets list of all orders")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<OrderData> getAllOrder() {
        return orderDto.gettingAllOrder();
    }


    @ApiOperation(value = "Gets a order by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable int id) throws ApiException {
        return orderDto.getOrderById(id);
    }


    @ApiOperation(value = "Edit a Order")
    // todo replace with logger
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void editOrder(@PathVariable int id, @RequestBody List<OrderItemForm> form) throws ApiException {
        orderDto.updateOrder(id, form);

    }

    @ApiOperation(value = "Download a pdf")
    // todo replace with logger
    @RequestMapping(path = "/{id}", method = RequestMethod.POST)

    public void downloadPdf(@PathVariable int id) throws ApiException {
        List<OrderItemData> orderItemDataList = orderDto.getOrderById(id);
        getEncodedPdf(orderItemDataList);
    }

    @ResponseBody
    private String getEncodedPdf(List<OrderItemData> invoiceDetails) throws RestClientException {
        String INVOICE_API_URL = "http://localhost:8000/invoice/api/generate";
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.postForObject(INVOICE_API_URL, invoiceDetails, String.class);
        Integer orderId = invoiceDetails.get(0).getOrderId();
        generatePdf(s, orderId);
        return s;
    }

    private void generatePdf(String b64, Integer orderId) {
        File file = new File("./order"+orderId+".pdf");

        try (FileOutputStream fos = new FileOutputStream(file);) {
            // To be short I use a corrupted PDF string, so make sure to use a valid one if you want to preview the PDF file
//            String b64 = "JVBERi0xLjUKJYCBgoMKMSAwIG9iago8PC9GaWx0ZXIvRmxhdGVEZWNvZGUvRmlyc3QgMTQxL04gMjAvTGVuZ3==";
            byte[] decoder = Base64.getDecoder().decode(b64);

            fos.write(decoder);
            System.out.println("PDF File Saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
