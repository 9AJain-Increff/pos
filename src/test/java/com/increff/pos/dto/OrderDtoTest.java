package com.increff.pos.dto;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.ConversionUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDtoTest extends AbstractUnitTest {
    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderItemService orderItemService;

    private List<ProductPojo> productList;
    private List<Pair<OrderPojo, List<OrderItemPojo>>> orderList;

    @Before
    public void setUp() throws ApiException {
        List<BrandPojo> brandList = MockUtil.setUpBrands(brandService);
        productList = MockUtil.setupProducts(brandList, productService);
        List<Integer> productIds = productList.stream().map(ProductPojo::getId).collect(Collectors.toList());
        MockUtil.setUpInventory(productIds, inventoryService);
        orderList = MockUtil.setUpMockOrders(orderService, orderItemService, inventoryService, productList);
    }

    @Test
    public void createOrder() throws ApiException {
        List<OrderItemForm> orderItemForms = Collections.singletonList(MockUtil.getMockOrderItemForm());
        OrderPojo expectedOrder = orderDto.addOrder(orderItemForms);

        Integer orderId = expectedOrder.getId();
        OrderPojo actualOrder = orderService.getOrderByOrderId(orderId);
        AssertUtil.assertEqualOrder(expectedOrder, actualOrder);

        List<OrderItemPojo> actualOrderItems = orderItemService.getOrderItemsById(orderId);
        List<OrderItemPojo> expectedOrderItems = orderItemForms
                .stream()
                .map(formItem -> convert(orderId, formItem))
                .collect(Collectors.toList());

        AssertUtil.assertEqualList(expectedOrderItems, actualOrderItems, AssertUtil::assertEqualOrderItems);
    }

    private OrderItemPojo convert(Integer orderId, OrderItemForm formItem) {
        OrderItemPojo orderItem = new OrderItemPojo();
        orderItem.setOrderId(orderId);
        orderItem.setQuantity(formItem.getQuantity());
        orderItem.setSellingPrice(formItem.getSellingPrice());
        try {
            ProductPojo product = productService.getProductByBarcode(formItem.getBarcode());
            orderItem.setProductId(product.getId());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return orderItem;
    }
    @Test
    public void updateOrder() throws ApiException {
        OrderPojo order = orderService.getAllOrders().get(0);
        List<OrderItemForm> orderItemForms = Arrays.asList(
                new OrderItemForm(productList.get(0).getBarcode(), 1, 1000.0f),
                new OrderItemForm(productList.get(1).getBarcode(), 1, 2500.0f)
        );

        Integer orderId = order.getId();
        OrderPojo updatedOrder = orderDto.updateOrder(orderId, orderItemForms);
        OrderPojo actualOrder = orderService.getOrderByOrderId(order.getId());

        AssertUtil.assertEqualOrder(updatedOrder, actualOrder);

        List<OrderItemPojo> actualItems = orderItemService.getOrderItemsById(order.getId());
        List<OrderItemPojo> expectedItems = Arrays.asList(
                new OrderItemPojo(null,  productList.get(0).getId(), 1, orderId, 1000.0f),
                new OrderItemPojo(null,  productList.get(1).getId(), 1, orderId, 2500.0f)
        );

        AssertUtil.assertEqualList(expectedItems, actualItems, AssertUtil::assertEqualOrderItems);
    }
    @Test
    public void getAll() {
        List<OrderData> actual = orderDto.getAllOrder();
        List<OrderData> expected = orderList.stream()
                .map(Pair::getKey)
                .map(ConversionUtil::convertToOrderData)
                .collect(Collectors.toList());
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualOrderData);
    }

    @Test
    public void testGetOrderById() throws ApiException {
        OrderPojo expected = orderList.get(0).getKey();
        OrderPojo actual = orderDto.getOrderById(expected.getId());
        AssertUtil.assertEqualOrder(expected, actual);
    }
//    @Test
//    public void getOrderDetails() throws ApiException {
//        OrderPojo order = orderList.get(0).getKey();
//        List<OrderItemPojo> orderItems = orderList.get(0).getValue();
//
////        OrderDetailsData orderDetails = orderApiDto.getOrderDetails(order.getId());
//
//        Assert.assertEquals(order.getId(), orderDetails.getOrderId());
//        Assert.assertEquals(order.getTime(), orderDetails.getTime());
//
//        List<OrderItemData> expectedOrderItems = getOrderItemData(orderItems);
//        List<OrderItemData> actualOrderItems = orderDetails.getItems();
//        AssertUtils.assertEqualList(expectedOrderItems, actualOrderItems, AssertUtils::assertEqualOrderItemData);
//    }
//    private List<OrderItemData> getOrderItemData(List<OrderItem> orderItems) {
//        return orderItems.stream().map(orderItem -> {
//            Product product = productList
//                    .stream()
//                    .filter(it -> it.getId().equals(orderItem.getProductId()))
//                    .findFirst()
//                    .orElse(null);
//            assert product != null;
//            return ConversionUtil.convertPojoToData(orderItem, product);
//        }).collect(Collectors.toList());
//    }

}
