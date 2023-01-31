package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.pos.util.ConversionUtil.convertToProductData;
import static org.junit.Assert.assertEquals;

@Component
public class MockUtil {

    public static BrandPojo getMockBrand() {
        BrandPojo mock = new BrandPojo();
        mock.setName("Mock Brand");
        mock.setCategory("Mock Category");
        return mock;
    }

    public static final List<BrandPojo> BRANDS = Arrays.asList(
            new BrandPojo(null, "apple", "phone"),
            new BrandPojo(null, "samsung", "phone"),
            new BrandPojo(null, "lenovo", "laptop"),
            new BrandPojo(null, "apple", "laptop"),
            new BrandPojo(null, "nike", "shoe")
    );

//        public static DailyReportPojo getMockPerDaySale() {
//        DailyReportPojo perDaySale = new DailyReportPojo();
//
//        perDaySale.setOrdersQuantity(1);
//        perDaySale.setRevenue(100.0f);
//        perDaySale.setDate(currentDate.minusDays(3));
//        perDaySale.setUniqueItemCount(3);
//        perDaySale.setTotalQuantityCount(5);
//
//        return perDaySale;
//    }


    public static List<BrandPojo> setUpBrands(BrandService brandService) {
        /*
         Total 5 mock brands are there:
         2 with category 'laptop', 2 with category 'phone' and 1 with 'shoe'
         */
        BRANDS.forEach(brand -> {
            try {
                brand.setId(null);
                brandService.addBrand(brand); // After add ID will be set
            } catch (ApiException ignored) {
            }
        });

        return BRANDS;
    }

    private static ProductPojo getProduct(String barcode, Integer brandId, String name, Float price) {
        return new ProductPojo(null, barcode, brandId, name, price);
    }

    private static final int BRAND_APPLE_PHONE = 0;
    private static final int BRAND_SAMSUNG_PHONE = 1;
    private static final int BRAND_LENOVO_LAPTOP = 2;
    private static final int BRAND_LENOVO_APPLE = 3;
    private static final int BRAND_NIKE_SHOE = 4;

    public static List<ProductPojo> setUpProductsAndInventory(
            List<BrandPojo> brands,
            ProductService productService,
            InventoryService inventoryService) {

        /* Created mock products representing every brand and category */
        List<ProductPojo> products = Arrays.asList(
                getProduct("a1001", brands.get(BRAND_APPLE_PHONE).getId(), "iphone x", 150000.0F),
                getProduct("a1002", brands.get(BRAND_APPLE_PHONE).getId(), "iphone se", 80000.0F),
                getProduct("a1003", brands.get(BRAND_SAMSUNG_PHONE).getId(), "galaxy fold", 180000.0F),
                getProduct("a1004", brands.get(BRAND_SAMSUNG_PHONE).getId(), "note 9", 130000.0F),
                getProduct("a1005", brands.get(BRAND_LENOVO_APPLE).getId(), "mac book pro", 250000.0F),
                getProduct("a1006", brands.get(BRAND_LENOVO_LAPTOP).getId(), "legion 5", 65000.0F),
                getProduct("a1007", brands.get(BRAND_NIKE_SHOE).getId(), "air jordan", 20000.0F)
        );

        products.forEach(product -> {
            try {
                productService.addProduct(product);
                InventoryPojo inventoryPojo = new InventoryPojo();
                inventoryPojo.setProductId(product.getId());
                inventoryPojo.setQuantity(10);
                inventoryService.addInventory(inventoryPojo);
            } catch (ApiException ignored) {
            }
        });

        return products;
    }

    public static List<InventoryData> getMockInventoryData() {
        return Arrays.asList(
                new InventoryData(1, "a1001", "iphone x", 10),
                new InventoryData(2, "a1002", "iphone se", 10),
                new InventoryData(3, "a1003", "galaxy fold", 10),
                new InventoryData(4, "a1004", "note 9", 10),
                new InventoryData(5, "a1005", "mac book pro", 10),
                new InventoryData(6, "a1006", "legion 5", 10),
                new InventoryData(7, "a1007", "air jordan", 10)
        );
    }

    public static OrderPojo getNewOrder() {
        OrderPojo order = new OrderPojo();
        order.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
        return order;
    }

    public static List<ProductPojo> setupProducts(List<BrandPojo> brands, ProductService productService) {

        /* Created mock products representing every brand and category */
        List<ProductPojo> products = Arrays.asList(
                getProduct("a1001", brands.get(BRAND_APPLE_PHONE).getId(), "iphone x", 150000.0f),
                getProduct("a1002", brands.get(BRAND_APPLE_PHONE).getId(), "iphone se", 80000.0f),
                getProduct("a1003", brands.get(BRAND_SAMSUNG_PHONE).getId(), "galaxy fold", 180000.0f),
                getProduct("a1004", brands.get(BRAND_SAMSUNG_PHONE).getId(), "note 9", 130000.0f),
                getProduct("a1005", brands.get(BRAND_LENOVO_APPLE).getId(), "mac book pro", 250000.0f),
                getProduct("a1006", brands.get(BRAND_LENOVO_LAPTOP).getId(), "legion 5", 65000.0f),
                getProduct("a1007", brands.get(BRAND_NIKE_SHOE).getId(), "air jordan", 20000.0f)
        );

        products.forEach(product -> {
            try {
                productService.addProduct(product);
            } catch (ApiException ignored) {
            }
        });

        return products;
    }

    public static List<ProductData> getMockProductDataList(List<BrandPojo> mockBrands, List<ProductPojo> mockProducts) {
        Map<Integer, BrandPojo> brandMap = getBrandMap(mockBrands);
        return mockProducts
                .stream()
                .map(product -> convertToProductData(product, brandMap.get(product.getBrandId())))
                .collect(Collectors.toList());
    }

    private static Map<Integer, BrandPojo> getBrandMap(List<BrandPojo> brands) {
        Map<Integer, BrandPojo> brandMap = new HashMap<>();
        brands.forEach(it -> brandMap.put(it.getId(), it));
        return brandMap;
    }

    public static ProductForm getMockProductForm() {
        ProductForm product = new ProductForm();
        product.setBarcode("a1001");
        product.setPrice(1800.0f);
        product.setName("Nike Shoes");
        product.setBarcode("ni0102");
        product.setBrandName("nike");
        product.setBrandCategory("shoe");
        return product;
    }

    public static InventoryPojo getMockInventory(Integer id) {
        InventoryPojo mock = new InventoryPojo();
        mock.setProductId(id);
        mock.setQuantity(10);
        return mock;
    }

    public static void setUpInventory(List<Integer> productIds, InventoryService inventoryService) throws ApiException {
        for (Integer productId : productIds) {
            InventoryPojo mockInventory = getMockInventory(productId);
            inventoryService.addInventory(mockInventory);
        }
    }

    private static final int PRODUCT_IPHONE_X = 0;
    private static final int PRODUCT_IPHONE_SE = 1;
    private static final int PRODUCT_GALAXY_FOLD = 2;
    private static final int PRODUCT_NOTE_9 = 3;
    private static final int PRODUCT_MAC_BOOK_PRO = 4;
    private static final int PRODUCT_LEGION_5 = 5;
    private static final int PRODUCT_AIR_JORDAN = 6;
    public static final LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);

    public static List<Pair<OrderPojo, List<OrderItemPojo>>> setUpMockOrders(
            OrderService orderService,
            OrderItemService orderItemService,
            InventoryService inventoryService,
            List<ProductPojo> products) throws ApiException {

        List<Pair<OrderPojo, List<OrderItemPojo>>> orders = new LinkedList<>();

        // Brand: Apple | Category: Phone, Laptop
        List<OrderItemPojo> items = Arrays.asList(
                new OrderItemPojo(null, products.get(PRODUCT_IPHONE_X).getId(), 2, null, 150000.0f), // Iphone X
                new OrderItemPojo(null, products.get(PRODUCT_MAC_BOOK_PRO).getId(), 1, null, 250000.0f) // MacBook
        );
        Pair<OrderPojo, List<OrderItemPojo>> order = addOrder(orderService, orderItemService, currentDate.minusDays(2), inventoryService, items);
        orders.add(order);

        // Brand: Samsung | Category: Phone
        items = Arrays.asList(
                new OrderItemPojo(null, products.get(PRODUCT_GALAXY_FOLD).getId(), 1, null, 180000.0f), // Galaxy Fold
                new OrderItemPojo(null, products.get(PRODUCT_NOTE_9).getId(), 1, null, 130000.0f)
        );
        order = addOrder(orderService, orderItemService, currentDate.minusDays(2), inventoryService, items); // Note 9
        orders.add(order);

        // Brand: Nike | Category: Shoe
        items = Collections.singletonList(
                new OrderItemPojo(null, products.get(PRODUCT_AIR_JORDAN).getId(), 3, null, 20000.0f)
        ); // Air Jordan
        order = addOrder(orderService, orderItemService, currentDate.minusDays(1), inventoryService, items);
        orders.add(order);

        // Brands: Apple, Lenovo | Category: Phone, Laptop
        items = Arrays.asList(
                new OrderItemPojo(null, products.get(PRODUCT_IPHONE_SE).getId(), 3, null, 80000.0f), // IPhone SE
                new OrderItemPojo(null, products.get(PRODUCT_LEGION_5).getId(), 3, null, 65000.0f) // Legion 5
        );
        order = addOrder(orderService, orderItemService, currentDate, inventoryService, items);
        orders.add(order);

        return orders;
    }

    public static Pair<OrderPojo, List<OrderItemPojo>> addOrder(
            OrderService orderService,
            OrderItemService orderItemService,
            LocalDateTime time,
            InventoryService inventoryService,
            List<OrderItemPojo> orderItems) throws ApiException {

        OrderPojo order = new OrderPojo(null, time, MOCK_INVOICE_PATH);
        orderService.addOrder(order);

        orderItems.forEach(item -> {
            item.setOrderId(order.getId());
            updateInventory(inventoryService, item.getProductId(), item.getQuantity());
        });

        orderItemService.addOrderItem(orderItems);
        return new Pair<>(order, orderItems);
    }

    private static final String MOCK_INVOICE_PATH = "mock invoice path";

    private static void updateInventory(InventoryService inventoryService, Integer productId, Integer required) {
        try {
            InventoryPojo inventory = inventoryService.getAndCheckInventoryByProductId(productId);
            inventory.setQuantity(inventory.getQuantity() - required);
            inventoryService.update(inventory);
        } catch (Exception ignored) {
        }
    }

    public static OrderItemForm getMockOrderItemForm() {
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setBarcode("a1001");
        orderItemForm.setQuantity(1);
        orderItemForm.setSellingPrice(1000.0f);
        return orderItemForm;
    }

    public static ProductPojo getMockProduct() {
        ProductPojo product = new ProductPojo();
        product.setBarcode("a1001");
        product.setPrice(1800.0f);
        product.setName("Nike Shoes");
        product.setBrandId(1);
        return product;
    }

    public static List<ProductPojo> getMockProducts(int size) {
        List<ProductPojo> products = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            ProductPojo product = getMockProduct();
            product.setBarcode(product.getBarcode() + i);
            product.setPrice(product.getPrice() + i);
            product.setName(product.getName() + i);
            products.add(product);
        }

        return products;
    }

    public static UserPojo getMockUser() {
        UserPojo mock = new UserPojo();
        mock.setEmail("mockuser@pos.com");
        mock.setPassword("pass@123");
        mock.setRole("operator");
        return mock;
    }
}
