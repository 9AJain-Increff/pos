package com.increff.pos.util;

import com.increff.pos.model.data.*;
import com.increff.pos.pojo.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssertUtil {

    public static <T> void assertEqualList(
            List<T> expectedList,
            List<T> actualList,
            AssertEqual<T> assertEqual) {

        assertEquals(expectedList.size(), actualList.size());

        for (int i = 0; i < expectedList.size(); i++) {
            T expected = expectedList.get(i);
            T actual = actualList.get(i);
            assertEqual.invoke(expected, actual);
        }
    }

    public static void assertEqualBrandData(BrandData expected, BrandData actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getName(), actual.getName());
    }

    public static void assertEqualBrands(BrandPojo expected, BrandPojo actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCategory(), actual.getCategory());
    }

    public interface AssertEqual<T> {

        void invoke(T expected, T actual);

    }

    public static void assertEqualInventoryData(InventoryData expected, InventoryData actual) {
        assertEquals(expected.getBarcode(), actual.getBarcode());
        assertEquals(expected.getProductName(), actual.getProductName());
        assertEquals(expected.getQuantity(), actual.getQuantity());
    }

    public static void assertEqualProducts(ProductPojo expected, ProductPojo actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBrandId(), actual.getBrandId());
        assertEquals(expected.getBarcode(), actual.getBarcode());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getName(), actual.getName());
    }

    public static void assertEqualProductData(ProductData expected, ProductData actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBrandName(), actual.getBrandName());
        assertEquals(expected.getBarcode(), actual.getBarcode());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getBrandCategory(), actual.getBrandCategory());
    }

    public static void assertEqualOrder(OrderPojo expected, OrderPojo actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCreatedOn(), actual.getCreatedOn());
        assertEquals(expected.getOrderURL(), actual.getOrderURL());
    }

    public static void assertEqualOrderItems(OrderItemPojo expected, OrderItemPojo actual) {
        assertEquals(expected.getOrderId(), actual.getOrderId());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getSellingPrice(), actual.getSellingPrice());
        assertEquals(expected.getProductId(), actual.getProductId());
    }

    public static void assertEqualOrderItemsData(OrderItemData expected, OrderItemData actual) {
        assertEquals(expected.getOrderId(), actual.getOrderId());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getSellingPrice(), actual.getSellingPrice());

    }

    public static void assertEqualOrderData(OrderData expected, OrderData actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCreatedOn(), actual.getCreatedOn());
    }

    public static void assertEqualInventory(InventoryPojo expected, InventoryPojo actual) {
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getProductId(), actual.getProductId());
    }

    public static void assertEqualOrders(OrderPojo expected, OrderPojo actual) {
        assertEquals(expected.getCreatedOn(), actual.getCreatedOn());

    }

    public static void assertEqualSaleReportData(SalesData expectedReport, SalesData actualReport) {
        assertEquals(expectedReport.getQuantity(), actualReport.getQuantity());
        assertEquals(expectedReport.getRevenue(), actualReport.getRevenue());
        assertEquals(expectedReport.getBrandName(), actualReport.getBrandName());
        assertEquals(expectedReport.getBrandCategory(), actualReport.getBrandCategory());
    }

    public static void assertEqualInventoryReportDate(
            InventoryReportData expectedInventoryReportData,
            InventoryReportData actualInventoryReportData) {

        assertEquals(expectedInventoryReportData.getBrandName(), actualInventoryReportData.getBrandName());
        assertEquals(expectedInventoryReportData.getBrandCategory(), actualInventoryReportData.getBrandCategory());
        assertEquals(expectedInventoryReportData.getQuantity(), actualInventoryReportData.getQuantity());
    }
}
