package com.increff.pos.dto;


import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.OrderItemsUpdates;
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

    private void validateInventory(
            List<InventoryPojo> inventoryPojoList,
            List<OrderItemPojo> orderItemPojoList) throws ApiException {

        for (int index = 0; index < inventoryPojoList.size(); index++) {
            int inventoryQuantity = inventoryPojoList.get(index).getQuantity();
            int requiredQuantity = orderItemPojoList.get(index).getQuantity();
            Integer productId = orderItemPojoList.get(index).getProductId();
            ProductPojo product = productService.checkProduct(productId);
            if (requiredQuantity > inventoryQuantity) {
                throw new ApiException("Only " + inventoryQuantity + " pieces of the product with barcode = " + product.getBarcode() + " exists");
            } else {
                inventoryPojoList.get(index).setQuantity(inventoryQuantity - requiredQuantity);
            }
        }
    }


    public List<String> getBarcodes(List<OrderItemPojo> orderItemPojos) throws ApiException {

        List<String> barcodes = new ArrayList<>();
        for (OrderItemPojo orderItem : orderItemPojos) {
            ProductPojo product = productService.checkProduct(orderItem.getProductId());
            barcodes.add(product.getBarcode());
        }
        return barcodes;
    }

    public List<InventoryPojo> getInventoryPojo(List<String> barcodes) throws ApiException {
        List<InventoryPojo> inventoryPojoList = new ArrayList<>();
        for (String barcode : barcodes) {
            ProductPojo product = productService.getProductByBarcode(barcode);
            InventoryPojo inventoryPojo = inventoryService.getAndCheckInventoryByProductId(product.getId());
            inventoryPojoList.add(inventoryPojo);
        }
        return inventoryPojoList;
    }

    public List<ProductPojo> getProductList(List<String> barcode) throws ApiException {
        List<ProductPojo> productPojoList = new ArrayList<>();
        for (String orderItemBarcode : barcode) {
            ProductPojo productPojo = productService.getProductByBarcode(orderItemBarcode);
            productPojoList.add(productPojo);
        }
        return productPojoList;
    }

    public void updateInventory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryService.update(inventoryPojo);
        }
    }

    public void addOrderItems(List<OrderItemPojo> addedOrderItems) throws ApiException {
        for (OrderItemPojo orderItemPojo : addedOrderItems) {
            ProductPojo product = productService.checkProduct(orderItemPojo.getProductId());
            orderItemPojo.setProductId(product.getId());
        }
        orderItemService.addOrderItem(addedOrderItems);

    }

    @Transactional(rollbackOn = Exception.class)
    public OrderData addOrder(List<OrderItemForm> orderItemForm) throws ApiException {
        if (orderItemForm.size() == 0) {
            throw new ApiException("Order must contain atleast 1 order item");
        }
        for (OrderItemForm orderItem : orderItemForm) {
            validateOrderForm(orderItem);
        }
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);
        OrderPojo newOrder = orderService.createNewOrder(convertToOrderPojo(date));
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        List<InventoryPojo> inventoryPojoList = new ArrayList<>();
        for (OrderItemForm orderItem : orderItemForm) {
            ProductPojo product = productService.getProductByBarcode(orderItem.getBarcode());
            InventoryPojo inventoryPojo = inventoryService.getAndCheckInventoryByProductId(product.getId());
            inventoryPojoList.add(inventoryPojo);
            orderItemPojoList.add(convertToOrderItemPojo(orderItem, newOrder.getId(), product.getId()));
            productService.validateSellingPrice(orderItem.getSellingPrice(), product.getPrice());
        }
        validateInventory(inventoryPojoList, orderItemPojoList);
        updateInventory(inventoryPojoList);
        addOrderItems(orderItemPojoList);
        List<InvoiceData> invoiceData = getInvoiceData(orderItemForm, newOrder);
        getEncodedPdf(invoiceData);
        addPdfURL(newOrder.getId());
        return convertToOrderData(newOrder);
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

    public void addNewlyUpdatedOrderItems(List<OrderItemPojo> addedOrderItems) throws ApiException {
        List<String> barcodes = getBarcodes(addedOrderItems);

        List<InventoryPojo> inventoryPojoList = getInventoryPojo(barcodes);
        validateInventory(inventoryPojoList, addedOrderItems);
        updateInventory(inventoryPojoList);
        addOrderItems(addedOrderItems);
    }

    public void updateOrderItems(List<OrderItemPojo> updatedOrderItems,
                                 Map<Integer, OrderItemPojo> productIdToOrderItemMapping) throws ApiException {
        List<String> barcodes = getBarcodes(updatedOrderItems);

        List<InventoryPojo> inventoryPojoList = getInventoryPojo(barcodes);
        validateInventory(inventoryPojoList, updatedOrderItems);
        updateInventory(inventoryPojoList);
        updateOrderItemList(updatedOrderItems, productIdToOrderItemMapping);

    }

    public void updateOrderItemList(List<OrderItemPojo> updatedOrderItems,
                                    Map<Integer, OrderItemPojo> barcodeToOrderItemMapping) throws ApiException {
        for (OrderItemPojo orderItemPojo : updatedOrderItems) {
            int newQuantity = barcodeToOrderItemMapping.get(orderItemPojo.getProductId()).getQuantity();
            orderItemPojo.setQuantity(newQuantity);
            orderItemService.updateOrderItem(orderItemPojo);
        }
    }

    public void deleteUpdatedOrderItems(List<OrderItemPojo> deletedOrderItems) throws ApiException {
        for (OrderItemPojo orderItemPojo : deletedOrderItems) {
            orderItemService.delete(orderItemPojo.getId());
        }
    }


    public void deleteOrderItems(List<OrderItemPojo> deletedOrderItems) throws ApiException {
        List<String> barcodes = getBarcodes(deletedOrderItems);

        List<InventoryPojo> inventoryPojoList = getInventoryPojo(barcodes);
        validateInventory(inventoryPojoList, deletedOrderItems);
        deleteInventory(inventoryPojoList);
        deleteUpdatedOrderItems(deletedOrderItems);
    }

    public void deleteInventory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryService.update(inventoryPojo);
        }
    }

    @Transactional(rollbackOn = ApiException.class)

    public OrderPojo updateOrder(Integer orderId, @NotNull List<OrderItemForm> orderItemForms) throws ApiException {
        if (orderItemForms.isEmpty()) {
            throw new ApiException("Add a Order Item");
        }
        List<OrderItemPojo> updatedOrderList = new ArrayList<>();
        for (OrderItemForm orderItem : orderItemForms) {

            validateOrderForm(orderItem);
            ProductPojo product = productService.getProductByBarcode(orderItem.getBarcode());
            productService.validateSellingPrice(orderItem.getSellingPrice(), product.getPrice());
            updatedOrderList.add(convertToOrderItemPojo(orderItem, orderId, product.getId()));
        }


        Map<Integer, OrderItemPojo> mappingFormData = new HashMap<>();

        List<OrderItemPojo> oldOrder = orderItemService.getOrderItemsById(orderId);
        Map<Integer, OrderItemPojo> productIdToOrderItemMapping = new HashMap<>();

        for (OrderItemPojo orderItemPojo : updatedOrderList) {
            ProductPojo product = productService.checkProduct(orderItemPojo.getProductId());
            productIdToOrderItemMapping.put(product.getId(), orderItemPojo);
            mappingFormData.put(product.getId(), orderItemPojo);
        }

        OrderItemsUpdates getlist = new OrderItemsUpdates();
        getlist.updatesList(oldOrder, mappingFormData, orderId);
        List<OrderItemPojo> updatedOrderItems = getlist.getToUpdate();
        List<OrderItemPojo> deletedOrderItems = getlist.getToDelete();
        List<OrderItemPojo> addedOrderItems = getlist.getToAdd();

        addNewlyUpdatedOrderItems(addedOrderItems);
        updateOrderItems(updatedOrderItems, productIdToOrderItemMapping);
        deleteOrderItems(deletedOrderItems);
        OrderPojo updatedOrder = orderService.getOrderByOrderId(orderId);
        List<InvoiceData> invoiceData = getInvoiceData(orderItemForms, updatedOrder);
        getEncodedPdf(invoiceData);
        addPdfURL(orderId);
        return updatedOrder;
    }

    public List<OrderData> getAllOrders() {
        List<OrderPojo> orders = orderService.getAllOrders();
        List<OrderData> ordersData = new ArrayList<OrderData>();
        for (OrderPojo p : orders) {
            ordersData.add(convertToOrderData(p));
        }
        return ordersData;
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

    public void addPdfURL(Integer id) {
        orderService.addPdfURL(id);
    }
}