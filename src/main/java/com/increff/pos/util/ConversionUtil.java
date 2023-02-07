package com.increff.pos.util;

import com.increff.pos.model.auth.UserRole;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;

import java.time.LocalDateTime;

public class ConversionUtil {


    public static BrandData convertToBrandData(BrandPojo p) {
        BrandData d = new BrandData();
        d.setName(p.getName());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    public static BrandPojo convertToBrandPojo(BrandForm f) {
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

    public static ProductPojo convertToProductPojo(ProductForm f, Integer brandId) {
        ProductPojo p = new ProductPojo();
        p.setName(f.getName());
        p.setBrandId(brandId);
        p.setPrice(f.getPrice());
        p.setBarcode(f.getBarcode());
        return p;
    }

    public static InventoryData convertToInventoryData(InventoryPojo p, String barcode) {
        InventoryData d = new InventoryData();
        d.setBarcode(barcode);
        d.setQuantity(p.getQuantity());
        return d;
    }

    public static InventoryData convertToInventoryData(InventoryPojo i, ProductPojo p) {

        InventoryData d = new InventoryData();
        d.setBarcode(p.getBarcode());
        d.setQuantity(i.getQuantity());
        d.setProductName(p.getName());
        d.setId(p.getId());
        return d;
    }

    public static InventoryPojo convertToInventoryPojo(InventoryForm f, Integer productId) {
        InventoryPojo p = new InventoryPojo();
        p.setProductId(productId);
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


    public static OrderItemData convertToOrderItemData(OrderItemPojo p, ProductPojo product, Integer orderId) {
        OrderItemData d = new OrderItemData();
        d.setSellingPrice(p.getSellingPrice());
        d.setQuantity(p.getQuantity());
        d.setOrderId(orderId);
        d.setId(p.getId());
        d.setName(product.getName());
        d.setBarcode(product.getBarcode());
        return d;
    }

    public static InvoiceData convertToInvoiceData(OrderItemForm p, ProductPojo product, Integer orderId) {
        InvoiceData d = new InvoiceData();
        d.setBarcode(p.getBarcode());
        d.setPrice(p.getSellingPrice());
        d.setQuantity(p.getQuantity());
        d.setName(product.getName());
        d.setOrderId(orderId);
        return d;
    }

    public static OrderItemPojo convertToOrderItemPojo(OrderItemForm o, Integer orderId, Integer productId) {
        OrderItemPojo p = new OrderItemPojo();
        p.setQuantity(o.getQuantity());
        p.setOrderId(orderId);
        p.setSellingPrice(o.getSellingPrice());
        p.setProductId(productId);
        return p;
    }

    public static DailyData convertToDailyData(DailyReportPojo d) {
        DailyData p = new DailyData();
        p.setDate(d.getDate());
        p.setRevenue(d.getRevenue());
        p.setOrderItemsQuantity(d.getOrderItemsQuantity());
        p.setOrdersQuantity(d.getOrdersQuantity());

        return p;
    }

    public static UserPojo convertToUserPojo(UserForm d) {
        UserPojo p = new UserPojo();
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
