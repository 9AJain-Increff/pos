package com.increff.pos.dto;


import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InvoiceData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.increff.pos.util.ConversionUtil.*;
import static com.increff.pos.util.ValidationUtil.*;

@Component
public class OrderDto {
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;

    public List<OrderItemData> getOrderItemsById(Integer orderId) throws ApiException {
        orderService.getOrderByOrderId(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsById(orderId);
        for (OrderItemPojo orderItem : orderItemPojoList) {
            ProductPojo product = productService.checkProduct(orderItem.getProductId());
            orderItemDataList.add(convertToOrderItemData(orderItem, product, orderId));
        }
        return orderItemDataList;
    }

    public OrderPojo getOrderById(Integer orderId) throws ApiException {
        return orderService.getOrderByOrderId(orderId);
    }

    @Transactional(rollbackOn = Exception.class)
    public OrderData addOrder(List<OrderItemForm> orderItemForm) throws ApiException {
        validateFormAndSellingPrice(orderItemForm);
        OrderPojo newOrder = createNewOrder();
        for (OrderItemForm orderItem : orderItemForm) {
            ProductPojo product = productService.getProductByBarcode(orderItem.getBarcode());
            OrderItemPojo orderItemPojo = (convertToOrderItemPojo(orderItem, newOrder.getId(), product.getId()));
            updateInventory(orderItemPojo);
            orderItemService.addOrderItem(orderItemPojo);
        }
        List<InvoiceData> invoiceData = getInvoiceData(orderItemForm, newOrder);
        getEncodedPdf(invoiceData);
        addPdfURL(newOrder.getId());
        return convertToOrderData(newOrder);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo updateOrder(Integer orderId, @NotNull List<OrderItemForm> orderItemForms) throws ApiException {
        validateFormAndSellingPrice(orderItemForms);
        List<OrderItemPojo> updatedOrderItems = new ArrayList<>();
        for (OrderItemForm orderItem : orderItemForms) {
            ProductPojo product = productService.getProductByBarcode(orderItem.getBarcode());
            updatedOrderItems.add(convertToOrderItemPojo(orderItem, orderId, product.getId()));
        }
        List<OrderItemPojo> previousOrderItems = orderItemService.getOrderItemsById(orderId);
        Map<Integer, OrderItemPojo> updatedOrderItemsMapping = new HashMap<>();
        Map<Integer, OrderItemPojo> previousOrderItemsMapping = new HashMap<>();
        for (OrderItemPojo orderItem : previousOrderItems) {
            previousOrderItemsMapping.put(orderItem.getProductId(), orderItem);
        }
        for (OrderItemPojo orderItem : updatedOrderItems) {
            updatedOrderItemsMapping.put(orderItem.getProductId(), orderItem);
        }
        updateAddedOrders(updatedOrderItemsMapping,previousOrderItemsMapping);
        updateUpdatedOrderItems(updatedOrderItemsMapping,previousOrderItemsMapping);
        deleteOrderItems(updatedOrderItemsMapping,previousOrderItemsMapping);
        OrderPojo order = orderService.getOrderByOrderId(orderId);
        List<InvoiceData> invoiceData = getInvoiceData(orderItemForms, order);
        getEncodedPdf(invoiceData);
        addPdfURL(order.getId());
        return orderService.getOrderByOrderId(orderId);
    }

    public List<OrderData> getAllOrders() {
        List<OrderPojo> orders = orderService.getAllOrders();
        List<OrderData> ordersData = new ArrayList<OrderData>();
        for (OrderPojo p : orders) {
            ordersData.add(convertToOrderData(p));
        }
        return ordersData;
    }
    public void addPdfURL(Integer id) {
        orderService.addPdfURL(id);
    }


    public List<InvoiceData> getInvoiceData(List<OrderItemForm> form, OrderPojo order) throws ApiException {
        List<InvoiceData> invoiceData = new ArrayList<>();
        for (int i = 0; i < form.size(); i++) {
            ProductPojo product = productService.getProductByBarcode(form.get(i).getBarcode());
            InvoiceData data = convertToInvoiceData(
                    form.get(i), product, order.getId()
            );
            invoiceData.add(data);
        }
        return invoiceData;
    }
    private void deleteOrderItems(Map<Integer, OrderItemPojo> updatedOrderItemsMapping,
                                   Map<Integer, OrderItemPojo> previousOrderItemsMapping) throws ApiException {
        Iterator<Map.Entry<Integer, OrderItemPojo>> iterator = previousOrderItemsMapping.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, OrderItemPojo> entry = iterator.next();
            if (!updatedOrderItemsMapping.containsKey(entry.getKey())) {
                OrderItemPojo orderItem = (entry.getValue());
                orderItem.setQuantity(-1*orderItem.getQuantity());
                updateInventory(orderItem);
                orderItemService.delete(orderItem.getId());
            }
        }

    }

    private OrderPojo createNewOrder(){
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);
        return orderService.createNewOrder(convertToOrderPojo(date));
    }

    private void updateAddedOrders(Map<Integer, OrderItemPojo> updatedOrderItemsMapping,
                                   Map<Integer, OrderItemPojo> previousOrderItemsMapping) throws ApiException {
        Iterator<Map.Entry<Integer, OrderItemPojo>> iterator = updatedOrderItemsMapping.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, OrderItemPojo> entry = iterator.next();
            if (!previousOrderItemsMapping.containsKey(entry.getKey())) {
                OrderItemPojo orderItem = (entry.getValue());
                updateInventory(orderItem);
                orderItemService.addOrderItem(orderItem);
            }
        }
    }
    private void updateUpdatedOrderItems(Map<Integer, OrderItemPojo> updatedOrderItemsMapping,
                                   Map<Integer, OrderItemPojo> previousOrderItemsMapping) throws ApiException {
        Iterator<Map.Entry<Integer, OrderItemPojo>> iterator = updatedOrderItemsMapping.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, OrderItemPojo> entry = iterator.next();
            if (previousOrderItemsMapping.containsKey(entry.getKey())) {
                OrderItemPojo updatedOrderItem = entry.getValue();
                OrderItemPojo previousOrderItem = (previousOrderItemsMapping.get(entry.getKey()));
                updatedOrderItem.setQuantity(updatedOrderItem.getQuantity()-previousOrderItem.getQuantity());
                updatedOrderItem.setId(previousOrderItem.getId());
                updateInventory(updatedOrderItem);
                updatedOrderItem.setQuantity(updatedOrderItem.getQuantity()+previousOrderItem.getQuantity());
                orderItemService.updateOrderItem(updatedOrderItem);
            }
        }
    }

    private void updateInventory(OrderItemPojo orderItem) throws ApiException {
            InventoryPojo inventory = inventoryService.validateInventory(orderItem.getProductId(),orderItem.getQuantity());
            inventory.setQuantity(inventory.getQuantity()-orderItem.getQuantity());
            inventoryService.update(inventory);
    }


    private String getEncodedPdf(List<InvoiceData> invoiceDetails) throws RestClientException {
        String INVOICE_API_URL = "http://localhost:8000/invoice/api/generate";
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.postForObject(INVOICE_API_URL, invoiceDetails, String.class);
        Integer orderId = invoiceDetails.get(0).getOrderId();
        generatePdf(s, orderId);
        return s;
    }

    private void generatePdf(String b64, Integer orderId) {
        File file = new File("./order" + orderId + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(file);) {
            byte[] decoder = Base64.getDecoder().decode(b64);

            fos.write(decoder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateFormAndSellingPrice(List<OrderItemForm> orderItemForms) throws ApiException {
        if (orderItemForms.isEmpty()) {
            throw new ApiException("Order List cannot be empty");
        }
        for (OrderItemForm orderItem : orderItemForms) {
            validateOrderForm(orderItem);
            ProductPojo product = productService.getProductByBarcode(orderItem.getBarcode());
            productService.validateSellingPrice(orderItem.getSellingPrice(),product.getPrice());
        }
    }

}