package com.increff.pos.util;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;

import static com.increff.pos.util.Normalization.normalize;

public class Normalizationutil {

    public static Float normalizeNumber(Float input) {
        String[] parts = input.toString().split("\\.");
        if (parts.length == 1) return input;

        String integerPart = parts[0];
        String decimalPart = parts[1];

        if (decimalPart.length() <= 2) return input;
        decimalPart = decimalPart.substring(0, 2);

        String doubleStr = integerPart + "." + decimalPart;
        return Float.parseFloat(doubleStr);
    }

    public static void normalizeBrand(BrandPojo brand) {
        brand.setName(normalize(brand.getName()));
        brand.setCategory(normalize(brand.getCategory()));
    }

    public static void normalizeProduct(ProductPojo p) {
        p.setBarcode(normalize(p.getBarcode()));
        p.setName(normalize(p.getName()));
        p.setPrice(normalizeNumber(p.getPrice()));
    }

    public static void normalizeOrderItem(OrderItemPojo p) {
        p.setSellingPrice(normalizeNumber(p.getSellingPrice()));
    }
}
