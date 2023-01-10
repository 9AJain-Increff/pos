package com.increff.employee.dto;


import com.increff.employee.model.OrderData;
import com.increff.employee.model.OrderItemData;
import com.increff.employee.model.OrderItemForm;
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

@Component
public class OrderDto {
    @Autowired
    private OrderService service;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private OrderItemService orderItemService;

    public List<OrderItemData> getOrderById(int id) throws ApiException {

        service.checkOrderExist(id);
        List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsById(id);
        List<ProductPojo> productPojoList = new ArrayList<>();
        for (OrderItemPojo pojo : orderItemPojoList) {
            productPojoList.add(productService.getProductByBarcode(pojo.getBarcode()));
        }
        List<OrderItemData> orderItemDataList = new ArrayList<>();

        for (int index = 0; index < orderItemPojoList.size(); index++) {
            String productName = productPojoList.get(index).getName();
            OrderItemPojo pojo = orderItemPojoList.get(index);
            orderItemDataList.add(convertToOrderItemData(pojo, productName));
        }
        return (orderItemDataList);
    }


    private void inventoryValidation(
            List<InventoryPojo> inventoryPojoList,
            List<OrderItemPojo> orderItemPojoList) throws ApiException {

        for (int index = 0; index < inventoryPojoList.size(); index++) {
            int inventoryQuantity = inventoryPojoList.get(index).getQuantity();
            int requiredQuantity = orderItemPojoList.get(index).getQuantity();
            String barcode = orderItemPojoList.get(index).getBarcode();

            if (requiredQuantity > inventoryQuantity) {
                throw new ApiException("Only " + inventoryQuantity + " pieces of the product with barcode = " + barcode + " exists");
            } else {
                inventoryPojoList.get(index).setQuantity(inventoryQuantity - requiredQuantity);
            }
        }
    }

    public void updateInventory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryService.update(inventoryPojo);
        }
    }

    public void deleteInventory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryService.update(inventoryPojo);
        }
    }

    public List<String> getBarcodes(List<OrderItemPojo> orderItemPojos) {

        return orderItemPojos.stream()
                .map(OrderItemPojo::getBarcode)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addOrder(List<OrderItemForm> orderItemForm) throws ApiException {

        List<String> barcodes = orderItemForm.stream()
                .map(OrderItemForm::getBarcode)
                .collect(Collectors.toList());

        List<InventoryPojo> inventoryPojoList = inventoryService.getInventoryPojo(barcodes);
        List<ProductPojo> productPojoList = productService.getProductPojo(barcodes);
        OrderPojo newOrder = service.createNewOrder();

        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        for (int i = 0; i < orderItemForm.size(); i++) {
            OrderItemPojo pojo = convertToOrderItemPojo(
                    productPojoList.get(i).getPrice(),
                    orderItemForm.get(i),
                    newOrder.getId()
            );
            orderItemPojoList.add(pojo);
        }

        inventoryValidation(inventoryPojoList, orderItemPojoList);
        updateInventory(inventoryPojoList);

        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemService.add(orderItemPojo);
        }

    }

    public void addUpdatedOrder(List<InventoryPojo> inventoryPojoList,
                                List<OrderItemPojo> orderItemPojoList) throws ApiException {
        inventoryValidation(inventoryPojoList, orderItemPojoList);

        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemService.add(orderItemPojo);
        }
    }

    public void deleting(int id) throws ApiException {
        orderItemService.delete(id);
    }

    private static class GetList {

        public GetList() {
            toAdd = new ArrayList<>();
            toDelete = new ArrayList<>();
            toUpdate = new ArrayList<>();
        }

        private List<OrderItemPojo> toUpdate, toAdd, toDelete;

        public List<OrderItemPojo> getToUpdate() {
            return toUpdate;
        }

        public List<OrderItemPojo> getToAdd() {
            return toAdd;
        }

        public List<OrderItemPojo> getToDelete() {
            return toDelete;
        }


        public void updatesList(List<OrderItemPojo> orderBeforeUpdate,
                                Map<String, OrderItemPojo> mapping, int orderId) {

            for (OrderItemPojo data : orderBeforeUpdate) {
                if (mapping.containsKey(data.getBarcode())) {
                    int requiredQuantity = mapping.get(data.getBarcode()).getQuantity();
                    data.setQuantity(requiredQuantity - data.getQuantity());
                    toUpdate.add(data);
                    mapping.remove(data.getBarcode());
                } else {
                    int requiredQuantity = data.getQuantity();
                    data.setQuantity(-1 * data.getQuantity());
                    toDelete.add(data);
                }
            }

            for (Map.Entry<String, OrderItemPojo> entry : mapping.entrySet()) {
                OrderItemPojo updatedOrderItemPojo = entry.getValue();
                updatedOrderItemPojo.setOrderId(orderId);
                toAdd.add(updatedOrderItemPojo);
            }
        }

    }

    public void addUpdatedOrder(List<OrderItemPojo> toAdd) throws ApiException {
        List<String> barcodes = getBarcodes(toAdd);

        List<InventoryPojo> inventoryPojoList = inventoryService.getInventoryPojo(barcodes);
        inventoryValidation(inventoryPojoList, toAdd);
        updateInventory(inventoryPojoList);
        for (OrderItemPojo orderItemPojo : toAdd) {
            orderItemService.add(orderItemPojo);
        }
    }

    public void updateOrder(List<OrderItemPojo> toUpdate,
                            Map<String, OrderItemPojo> copyMapping) throws ApiException {
        List<String> barcodes = getBarcodes(toUpdate);

        List<InventoryPojo> inventoryPojoList = inventoryService.getInventoryPojo(barcodes);
        List<ProductPojo> productPojoList = productService.getProductPojo(barcodes);
        inventoryValidation(inventoryPojoList, toUpdate);
        updateInventory(inventoryPojoList);
        for (OrderItemPojo orderItemPojo : toUpdate) {
            int newQuantity = copyMapping.get(orderItemPojo.getBarcode()).getQuantity();
            orderItemPojo.setQuantity(newQuantity);
            orderItemService.updateOrderItem(orderItemPojo);
        }
    }

    public void deleteOrder(List<OrderItemPojo> toDelete,
                            Map<String, OrderItemPojo> copyMapping) throws ApiException {
        List<String> barcodes = getBarcodes(toDelete);

        List<InventoryPojo> inventoryPojoList = inventoryService.getInventoryPojo(barcodes);

        inventoryValidation(inventoryPojoList, toDelete);
        deleteInventory(inventoryPojoList);

        for (OrderItemPojo orderItemPojo : toDelete) {
            orderItemService.delete(orderItemPojo.getId());
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updating(int id, @NotNull List<OrderItemForm> orderItemForms) throws ApiException {
        if (orderItemForms.isEmpty()) {
            throw new ApiException("Add a Order Item");
        }

        List<OrderItemPojo> updatedOrder = orderItemForms
                .stream()
                .map(orderItemForm -> convertToOrderItemPojo(orderItemForm.getPrice(), orderItemForm, id))
                .collect(Collectors.toList());

        Map<String, OrderItemPojo> mappingFormData = new HashMap<>();

        List<OrderItemPojo> oldOrder = orderItemService.getOrderItemsById(id);
        Map<String, OrderItemPojo> copyMapping = new HashMap<>();
        for (OrderItemPojo temp : updatedOrder) {
            copyMapping.put(temp.getBarcode(), temp);
            mappingFormData.put(temp.getBarcode(), temp);
        }

        GetList getlist = new GetList();
        getlist.updatesList(oldOrder, mappingFormData, id);
        List<OrderItemPojo> toUpdate = getlist.getToUpdate();
        List<OrderItemPojo> toDelete = getlist.getToDelete();
        List<OrderItemPojo> toAdd = getlist.getToAdd();

        addUpdatedOrder(toAdd);
        updateOrder(toUpdate, copyMapping);
        deleteOrder(toDelete, copyMapping);


    }


    public List<OrderData> gettingAllOrder() {
        List<OrderPojo> list = service.getAll();
        System.out.println(list);
        List<OrderData> list2 = new ArrayList<OrderData>();
        for (OrderPojo p : list) {
            list2.add(convertToOrderData(p));
        }
        return list2;
    }


    private void checkAdd(InventoryPojo inventoryPojo, int extraRequired) throws ApiException {
        if (extraRequired > inventoryPojo.getQuantity()) {
            throw new ApiException("Maximum available quantity for the barcode" + inventoryPojo.getBarcode() + " is " + inventoryPojo.getQuantity());
        } else {
            System.out.println("3");
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - extraRequired);
            System.out.println("4");
            inventoryService.update(inventoryPojo);

        }
    }

}
