package com.increff.employee.controller;

import com.increff.employee.dto.OrderDto;
import com.increff.employee.model.*;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
        List<InvoiceData> invoiceData = orderDto.addOrder(form);
        getEncodedPdf(invoiceData);
        int orderId = invoiceData.get(0).getOrderId();
        orderDto.addPdfURL(orderId);
    }

    @ApiOperation(value = "Get a Pdf Url")
    // todo change path
    @RequestMapping(path = "/getPdf/{id}", method = RequestMethod.GET)
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
//        orderDto.updateOrder(id, form);
        List<InvoiceData> invoiceData = orderDto.updateOrder(id,form);
        getEncodedPdf(invoiceData);
        orderDto.addPdfURL(id);
    }

    @ApiOperation(value = "Download a pdf")
    // todo replace with logger
    @RequestMapping(path = "/{id}", method = RequestMethod.POST)

//    public void downloadPdf(@PathVariable int id) throws ApiException {
//        List<OrderItemData> orderItemDataList = orderDto.getOrderById(id);
//        getEncodedPdf(orderItemDataList);
//    }

    @ResponseBody
    private String getEncodedPdf(List<InvoiceData> invoiceDetails) throws RestClientException {
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
