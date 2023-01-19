package com.increff.employee.util;

import com.increff.employee.model.*;
import com.increff.employee.pojo.*;
import com.sun.xml.bind.v2.model.core.ID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversionUtil {


    public static BrandData convertToBrandData(BrandPojo p) {
        BrandData d = new BrandData();
        d.setName(p.getName());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    public static  BrandPojo convertToBrandPojo(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setName(f.getName());
        p.setCategory(f.getCategory());
        return p;
    }


    public static ProductData convertToProductData(ProductPojo p, BrandPojo b) {
        ProductData d = new ProductData();
        d.setName(p.getName());
        d.setBrandName(b.getName());
        d.setBrandCategory(b.getCategory());
        d.setBarcode(p.getBarcode());
        d.setPrice(p.getPrice());
        d.setId(p.getId());
        return d;
    }

    public static  ProductPojo convertToProductPojo(ProductForm f, int brandId) {
        ProductPojo p = new ProductPojo();
        p.setName(f.getName());
        p.setBrandId(brandId);
        p.setPrice(f.getPrice());
        p.setBarcode(f.getBarcode());
        return p;
    }

    public static InventoryData convertToInventoryData(InventoryPojo p) {
        InventoryData d = new InventoryData();
        d.setBarcode(p.getBarcode());
        d.setQuantity(p.getQuantity());
        return d;
    }

    public static InventoryData convertToInventoryData(InventoryPojo i,ProductPojo p) {

        InventoryData d = new InventoryData();
        d.setBarcode(p.getBarcode());
        d.setQuantity(i.getQuantity());
        d.setProductName(p.getName());
        d.setId(p.getId());
        return d;
    }

    public static  InventoryPojo convertToInventoryPojo(InventoryForm f) {
        InventoryPojo p = new InventoryPojo();
        p.setBarcode(f.getBarcode());
        p.setQuantity(f.getQuantity());
        return p;
    }




    public static OrderPojo convertToOrderPojo(LocalDateTime d) {
        OrderPojo o = new OrderPojo();
        o.setCreatedOn(d);
        return o;
    }
    public static OrderData convertToOrderData(OrderPojo p) {
        OrderData d = new OrderData();
        d.setId(p.getId());
        d.setCreatedOn(p.getCreatedOn());
        return d;
    }


    public static OrderItemData convertToOrderItemData(OrderItemPojo p, String productName) {
        OrderItemData d = new OrderItemData();
        d.setBarcode(p.getBarcode());
        d.setPrice(p.getPrice());
        d.setQuantity(p.getQuantity());
        d.setOrderId(p.getOrderId());
        d.setId(p.getId());
        d.setName(productName);
        return d;
    }
    public static InvoiceData convertToInvoiceData(OrderItemForm p, Integer id) {
        InvoiceData d = new InvoiceData();
        d.setBarcode(p.getBarcode());
        d.setPrice(p.getPrice());
        d.setQuantity(p.getQuantity());
        d.setName(p.getName());
        d.setOrderId(id);
        return d;
    }
    public static OrderItemData convertToOrderItemData(OrderItemForm p, Integer id) {
        OrderItemData d = new OrderItemData();
        d.setBarcode(p.getBarcode());
        d.setPrice(p.getPrice());
        d.setQuantity(p.getQuantity());
        d.setOrderId(p.getOrderId());
        d.setName(p.getName());
        d.setId(id);
        return d;
    }


    public static  OrderItemPojo convertToOrderItemPojo(Float price,OrderItemForm o, int id) {
        OrderItemPojo p = new OrderItemPojo();
        p.setQuantity(o.getQuantity());
        p.setOrderId(id);
        p.setBarcode(o.getBarcode());
        p.setPrice(price);

        return p;
    }

    public static  DailyData convertToDailyData(DailyReportPojo d) {
        DailyData p = new DailyData();
        p.setDate(d.getDate());
        p.setRevenue(d.getRevenue());
        p.setOrderItemsQuantity(d.getOrderItemsQuantity());
        p.setOrdersQuantity(d.getOrdersQuantity());

        return p;
    }
    public static  UserPojo convertToUserPojo(UserForm d) {
        UserPojo p = new UserPojo();
        p.setRole(d.getRole());
        p.setEmail(d.getEmail());
        p.setPassword(d.getPassword());
        return p;
    }

//        private static  OrderPojo convertToOrderPojo(OrderItemForm f) {
//        OrderItemPojo p = new OrderPojo();
//        p.setQuantity(f.getQuantity());
//        p.setBarcode(f.getBarcode());
//        p.setPrice(f.getPrice());
//        p.setOrderId(f.());
//        p.setBrandName(f.getBrandName());
//        p.setBarcode(f.getBarcode());
//        return p;
//    }


}
