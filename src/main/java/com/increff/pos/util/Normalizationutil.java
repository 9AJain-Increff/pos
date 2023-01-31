package com.increff.pos.util;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;

import static com.increff.pos.util.Normalization.normalize;

public class Normalizationutil {

    public static void normalizeBrand(BrandPojo brand) {
        brand.setName(normalize(brand.getName()));
        brand.setCategory(normalize(brand.getCategory()));
    }

    public static void normalizeProduct(ProductPojo p) {
        p.setBarcode(normalize(p.getBarcode()));
        p.setName(normalize(p.getName()));
        p.setPrice(normalize(p.getPrice()));
    }

    public static void normalizeOrderItem(OrderItemPojo p) {
        p.setSellingPrice(normalize(p.getSellingPrice()));
    }
}
