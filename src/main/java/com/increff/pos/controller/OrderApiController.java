package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.*;
import com.increff.pos.service.ApiException;
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
// TODO: 29/01/23 remove commented code
// TODO: 29/01/23 every add/edit method should return corresponding data
@RequestMapping(path = "/api/orders")
public class OrderApiController {


    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds a order")
    // todo make it orderData
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void addOrder(@RequestBody List<OrderItemForm> form) throws ApiException {
        List<InvoiceData> invoiceData = orderDto.addOrder(form);
        getEncodedPdf(invoiceData);
        // TODO: 29/01/23 if there are no orderitems will this work?
        int orderId = invoiceData.get(0).getOrderId();
        // TODO: 29/01/23 create pdf inside DTO
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

    // TODO: 29/01/23 getAllOrders
    @ApiOperation(value = "Gets list of all orders")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<OrderData> getAllOrder() {
        return orderDto.getAllOrder();
    }


    @ApiOperation(value = "Gets a order by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable int id) throws ApiException {
        return orderDto.getOrderById(id);
    }


    @ApiOperation(value = "Edit a Order")
    // TODO: 29/01/23 move pdf gen to dto
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
        // TODO: 29/01/23 Create a sep class Constants and declare this there
        String INVOICE_API_URL = "http://localhost:8000/invoice/api/generate";
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.postForObject(INVOICE_API_URL, invoiceDetails, String.class);
        Integer orderId = invoiceDetails.get(0).getOrderId();
        generatePdf(s, orderId);
        return s;
    }

    // TODO: 29/01/23 replace system.out with logger
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
