package com.increff.pos.dto;


import com.increff.pos.model.*;
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
import java.util.stream.Collectors;

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

    // TODO: 29/01/23 dont depend on index
    public List<OrderItemData> getOrderItemsById(Integer orderId) throws ApiException {

        orderService.checkOrderExist(orderId);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsById(orderId);
        List<String> barcodes = getBarcodes(orderItemPojoList);
        List<ProductPojo> productPojoList = getProductList(barcodes);
        List<OrderItemData> orderItemDataList = new ArrayList<>();

        for (int index = 0; index < orderItemPojoList.size(); index++) {
            ProductPojo product= productPojoList.get(index);

            OrderItemPojo orderItem = orderItemPojoList.get(index);
            orderItemDataList.add(convertToOrderItemData(orderItem, product, orderId));
        }
        return (orderItemDataList);
    }

    public OrderPojo getOrderById(Integer orderId){
        return orderService.getOrderByOrderId(orderId);
    }

    // TODO: 29/01/23 can use get by order id method instead of having a sep method
    public String getOrderPdf(Integer id) throws ApiException {
        return orderService.getPdfUrl(id);
    }

    // TODO: 29/01/23 dont depend on list index. Iterate over the items and do the required things
    private void validateInventory(
            List<InventoryPojo> inventoryPojoList,
            List<OrderItemPojo> orderItemPojoList) throws ApiException {

        for (int index = 0; index < inventoryPojoList.size(); index++) {
            int inventoryQuantity = inventoryPojoList.get(index).getQuantity();
            int requiredQuantity = orderItemPojoList.get(index).getQuantity();
            Integer productId = orderItemPojoList.get(index).getProductId();
            ProductPojo product = productService.getProductById(productId);
            if (requiredQuantity > inventoryQuantity) {
                throw new ApiException("Only " + inventoryQuantity + " pieces of the product with barcode = " + product.getBarcode() + " exists");
            } else {
                inventoryPojoList.get(index).setQuantity(inventoryQuantity - requiredQuantity);
            }
        }
    }


    public List<String> getBarcodes(List<OrderItemPojo> orderItemPojos) throws ApiException {

        List<String> barcodes = new ArrayList<>();
        for(OrderItemPojo orderItem : orderItemPojos){
            ProductPojo product = productService.getProductById(orderItem.getProductId());
            barcodes.add(product.getBarcode());
        }
        return barcodes;
    }

    // TODO: 29/01/23 can be placed inside inventory api and use ids
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

    // TODO: 29/01/23 can be placed inside inventory api
    public void updateInventory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryService.update(inventoryPojo);
        }
    }
    public void addOrderItems(List<OrderItemPojo> addedOrderItems) throws ApiException {
        for (OrderItemPojo orderItemPojo : addedOrderItems) {
            ProductPojo product = productService.getProductById(orderItemPojo.getProductId());
            orderItemPojo.setProductId(product.getId());
        }
            orderItemService.addOrderItem(addedOrderItems);

    }

    // TODO: 29/01/23 remove unused variables
    // TODO: 29/01/23 try to reduce the DB calls
    @Transactional(rollbackOn = ApiException.class)
    public OrderData addOrder(List<OrderItemForm> orderItemForm) throws ApiException {
        if(orderItemForm.size() == 0){
            throw new ApiException("Order must contain atleast 1 order item");
        }
        for (OrderItemForm orderItem : orderItemForm) {
            validateOrderForm(orderItem);
        }
        List<String> barcodes = orderItemForm.stream()
                .map(OrderItemForm::getBarcode)
                .collect(Collectors.toList());

        List<InventoryPojo> inventoryPojoList = getInventoryPojo(barcodes);
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);
        OrderPojo newOrder = orderService.createNewOrder(convertToOrderPojo(date));
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        for (int i = 0; i < orderItemForm.size(); i++) {
            ProductPojo product = productService.getProductByBarcode(orderItemForm.get(i).getBarcode());
            productService.validateSellingPrice(orderItemForm.get(i).getSellingPrice(),product.getPrice());
            // TODO: 29/01/23 where are you validating selling price? Place it in productService
            OrderItemPojo pojo = convertToOrderItemPojo(
                    orderItemForm.get(i).getSellingPrice(),
                    orderItemForm.get(i),
                    newOrder.getId(),
                    product.getId()
            );
            orderItemPojoList.add(pojo);
        }

        validateInventory(inventoryPojoList, orderItemPojoList);
        updateInventory(inventoryPojoList);
        addOrderItems(orderItemPojoList);

        List<InvoiceData> invoiceData = getInvoiceData(orderItemForm, newOrder);
        getEncodedPdf(invoiceData);
        // FIXED: 29/01/23 if there are no orderitems will this work?
        int orderId = invoiceData.get(0).getOrderId();
        // FIXED: 29/01/23 create pdf inside DTO
        addPdfURL(orderId);
        return convertToOrderData(newOrder);

    }
    private String getEncodedPdf(List<InvoiceData> invoiceDetails) throws RestClientException {
        // TODO: 29/01/23 Create a sep class Constants and declare this there
        String INVOICE_API_URL = "http://localhost:8000/invoice/api/generate";
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.postForObject(INVOICE_API_URL, invoiceDetails, String.class);
        Integer orderId = invoiceDetails.get(0).getOrderId();
        generatePdf(s, orderId);
        return s;
    }

    private void generatePdf(String b64, Integer orderId) {
        File file = new File("./order"+orderId+".pdf");

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
        List<ProductPojo> productPojoList = getProductList(barcodes);
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

        // TODO: 29/01/23 why to validate the inventory while deleting?
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
    // TODO: 29/01/23 reduce db calls
    public OrderPojo updateOrder(Integer orderId, @NotNull List<OrderItemForm> orderItemForms) throws ApiException {
        if (orderItemForms.isEmpty()) {
            throw new ApiException("Add a Order Item");
        }
        List<OrderItemPojo> updatedOrderList = new ArrayList<>();
        for (OrderItemForm orderItem : orderItemForms) {

            validateOrderForm(orderItem);
            ProductPojo product =productService.getProductByBarcode(orderItem.getBarcode());
            productService.validateSellingPrice(orderItem.getSellingPrice(),product.getPrice());
            updatedOrderList.add(convertToOrderItemPojo(orderItem.getSellingPrice(), orderItem, orderId, product.getId()));
        }


        Map<Integer, OrderItemPojo> mappingFormData = new HashMap<>();

        List<OrderItemPojo> oldOrder = orderItemService.getOrderItemsById(orderId);
        Map<Integer, OrderItemPojo> productIdToOrderItemMapping = new HashMap<>();
        // TODO: 29/01/23 rename variables properly
        for (OrderItemPojo temp : updatedOrderList) {
            ProductPojo product = productService.getProductById(temp.getProductId());
            productIdToOrderItemMapping.put(product.getId(), temp);
            mappingFormData.put(product.getId(), temp);
        }

        // TODO: 29/01/23 rename classes and variables properly
        GetList getlist = new GetList();
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

    // TODO: 29/01/23 why sep method? you can directly call this one liner from the invocation place
    public List<InvoiceData> getInvoiceData(List<OrderItemForm> form, OrderPojo order) throws ApiException {
        List<InvoiceData> invoiceData = new ArrayList<>();
        for (int i = 0; i < form.size(); i++) {
            ProductPojo product = productService.getProductByBarcode(form.get(i).getBarcode());
            InvoiceData data = convertToInvoiceData(
                    form.get(i),
                    product,
                    order.getId()
            );
            invoiceData.add(data);
        }
        return invoiceData;
    }
    public void  addPdfURL(Integer id){
        orderService.addPdfURL(id);
    }

}
