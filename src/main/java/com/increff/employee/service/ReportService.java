package com.increff.employee.service;

import com.increff.employee.dto.ReportDto;
import com.increff.employee.model.SalesData;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    private Map<String,Integer> getQuantityMapping(List<OrderItemPojo> orderItems){
        Map<String, Integer> mapping = new HashMap<>();
        for(OrderItemPojo orderItem : orderItems){
            String barcode =orderItem.getBarcode();
            if(mapping.containsKey(barcode)){
                mapping.put(barcode, mapping.get(barcode)+orderItem.getQuantity());
            }
            else{
                mapping.put(barcode,orderItem.getQuantity());
            }
        }
        return mapping;
    }
    public List<SalesData> getSalesReport(
            String brandName,
            String BrandCategory,
            LocalDateTime startTime,
            LocalDateTime endTime) throws ApiException {
        List<OrderPojo> orders = orderService.getOrdersBetweenTime(startTime, endTime);

        List<OrderItemPojo> orderItems = orderItemService.getOrderItemByOrders(orders);
        List<String> barcodes =    orderItems.stream()
                .map(OrderItemPojo::getBarcode)
                .collect(Collectors.toList());

        Map<String, ProductPojo> barcodeToProductMapping =  productService.getProductsBybarcodes(barcodes);
        Map<String,Integer> barcodeToQuantityMapping = getQuantityMapping(orderItems);

        List<SalesData> salesData = new ArrayList<>();
        Iterator<Map.Entry<String, ProductPojo>> itr = barcodeToProductMapping.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, ProductPojo> entry = itr.next();
            String barcode = entry.getKey();
            ProductPojo product = entry.getValue();
            BrandPojo brand = brandService.getBrandById(product.getBrandId());
            SalesData saleData = new SalesData();
            saleData.setQuantity(barcodeToQuantityMapping.get(barcode));
            saleData.setBrandCategory(brand.getCategory());
            saleData.setBrandName(brand.getName());
            saleData.setRevenue(barcodeToQuantityMapping.get(barcode)*product.getPrice());
            salesData.add(saleData);
        }

        return salesData;

    }

}
