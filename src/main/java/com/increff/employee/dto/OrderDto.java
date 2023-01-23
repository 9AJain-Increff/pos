package com.increff.employee.dto;


import com.increff.employee.model.*;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.employee.util.ConversionUtil.*;
import static com.increff.employee.util.Normalization.normalize;
import static com.increff.employee.util.ValidationUtil.isBlank;
import static com.increff.employee.util.ValidationUtil.isNegative;

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

    public List<OrderItemData> getOrderById(int id) throws ApiException {

        orderService.checkOrderExist(id);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsById(id);
        List<String> barcodes = getBarcodes(orderItemPojoList);
        List<ProductPojo> productPojoList = getProductList(barcodes);
        List<OrderItemData> orderItemDataList = new ArrayList<>();

        for (int index = 0; index < orderItemPojoList.size(); index++) {
            ProductPojo product= productPojoList.get(index);

            OrderItemPojo orderItem = orderItemPojoList.get(index);
            orderItemDataList.add(convertToOrderItemData(orderItem, product));
        }
        return (orderItemDataList);
    }

    public String getOrderPdf(Integer id) throws ApiException {
        return orderService.getPdfUrl(id);
    }

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
            ProductPojo productPojo = productService.getAndCheckProductByBarcode(orderItemBarcode);
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
            ProductPojo product = productService.getProductById(orderItemPojo.getProductId());
            orderItemPojo.setProductId(product.getId());
            orderItemService.addOrderItem(orderItemPojo);
        }
    }
    @Transactional(rollbackOn = ApiException.class)
    public List<InvoiceData> addOrder(List<OrderItemForm> orderItemForm) throws ApiException {
        for (OrderItemForm orderItem : orderItemForm) {
            normalizeFormData(orderItem);
            validateFormData(orderItem);
        }
        List<String> barcodes = orderItemForm.stream()
                .map(OrderItemForm::getBarcode)
                .collect(Collectors.toList());

        List<InventoryPojo> inventoryPojoList = getInventoryPojo(barcodes);
        List<ProductPojo> productPojoList = getProductList(barcodes);
        OrderPojo newOrder = orderService.createNewOrder();

        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        for (int i = 0; i < orderItemForm.size(); i++) {
            ProductPojo product = productService.getProductByBarcode(orderItemForm.get(i).getBarcode());
            OrderItemPojo pojo = convertToOrderItemPojo(
                    productPojoList.get(i).getPrice(),
                    orderItemForm.get(i),
                    newOrder.getId(),
                    product.getId()
            );
            orderItemPojoList.add(pojo);
        }

        validateInventory(inventoryPojoList, orderItemPojoList);
        updateInventory(inventoryPojoList);
        addOrderItems(orderItemPojoList);
        List<InvoiceData> invoiceData = new ArrayList<>();
        for (int i = 0; i < orderItemForm.size(); i++) {
            InvoiceData data = convertToInvoiceData(
                    orderItemForm.get(i),
                    newOrder.getId()
            );
            invoiceData.add(data);
        }
        return invoiceData;
    }

//    public void deleting(int id) throws ApiException {
//        orderItemService.delete(id);
//    }

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
    public List<InvoiceData> updateOrder(int id, @NotNull List<OrderItemForm> orderItemForms) throws ApiException {
        if (orderItemForms.isEmpty()) {
            throw new ApiException("Add a Order Item");
        }
        List<OrderItemPojo> updatedOrder = new ArrayList<>();
        for (OrderItemForm orderItem : orderItemForms) {
            normalizeFormData(orderItem);
            validateFormData(orderItem);
            ProductPojo product =productService.getProductByBarcode(orderItem.getBarcode());
            updatedOrder.add(convertToOrderItemPojo(orderItem.getPrice(), orderItem, id, product.getId()));
        }


        Map<Integer, OrderItemPojo> mappingFormData = new HashMap<>();

        List<OrderItemPojo> oldOrder = orderItemService.getOrderItemsById(id);
        Map<Integer, OrderItemPojo> productToToOrderItemMapping = new HashMap<>();
        for (OrderItemPojo temp : updatedOrder) {
            ProductPojo product = productService.getProductById(temp.getProductId());
            productToToOrderItemMapping.put(product.getId(), temp);
            mappingFormData.put(product.getId(), temp);
        }

        GetList getlist = new GetList();
        getlist.updatesList(oldOrder, mappingFormData, id);
        List<OrderItemPojo> updatedOrderItems = getlist.getToUpdate();
        List<OrderItemPojo> deletedOrderItems = getlist.getToDelete();
        List<OrderItemPojo> addedOrderItems = getlist.getToAdd();

        addNewlyUpdatedOrderItems(addedOrderItems);
        updateOrderItems(updatedOrderItems, productToToOrderItemMapping);
        deleteOrderItems(deletedOrderItems);
        List<InvoiceData> invoiceData = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemForms) {
            InvoiceData invoice = convertToInvoiceData(orderItemForm, id);
            invoiceData.add(invoice);
        }
        return invoiceData;
    }


    public List<OrderData> getAllOrder() {
        List<OrderPojo> orders = orderService.getAllOrders();
        List<OrderData> ordersData = new ArrayList<OrderData>();
        for (OrderPojo p : orders) {
            ordersData.add(convertToOrderData(p));
        }
        return ordersData;
    }

    public void  addPdfURL(Integer id){
        orderService.addPdfURL(id);
    }
    private void validateFormData(OrderItemForm form) throws ApiException {

        if (isBlank(form.getBarcode())) {
            throw new ApiException("brand cannot be empty");
        }

        if (isNegative(form.getPrice())) {
            throw new ApiException("enter a valid price");
        }
        if (isNegative(form.getQuantity())) {
            throw new ApiException("enter a valid quantity");
        }

    }

    private void normalizeFormData(OrderItemForm form) {
//        form.setName(normalize(form.getName()));
        form.setBarcode(normalize(form.getBarcode()));
    }

}
