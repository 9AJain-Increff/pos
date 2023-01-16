package com.increff.employee.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.increff.employee.dao.OrderDao;
import com.increff.employee.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.employee.util.ConversionUtil.convertToOrderPojo;

@Service
public class OrderService {

    @Autowired
    private OrderDao dao;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;
    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo add(OrderPojo p) throws ApiException {
        normalize(p);
        return dao.insert(p);
    }
    @Transactional
    public void delete(int id) {
        dao.delete(id);
    }




    @Transactional
    public List<OrderPojo> getAll() {
        return dao.selectAll();
    }
    public OrderPojo createNewOrder() throws ApiException {
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);
        OrderPojo newOrder = convertToOrderPojo(date);
        return dao.insert(newOrder);
    }

//    @Transactional(rollbackOn  = ApiException.class)
//    public void update(int id, OrderPojo p) throws ApiException {
//        normalize(p);
//        OrderPojo ex = getCheck(id);
//        System.out.println(p.getName());
//        ex.setName(p.getName());
//        ex.setPrice(p.getPrice());
////        else{
////            throw new ApiException("order name + category already exists");
////        }
//
////        dao.update(ex);
//    }

    public List<InventoryPojo> getInventoryPojo(List<String> barcode) throws ApiException {
        List<InventoryPojo> inventoryPojoList = new ArrayList<>();
        for (String temp : barcode) {
            InventoryPojo inventoryPojo = inventoryService.getAndCheckInventoryByBarcode(temp);
            inventoryPojoList.add(inventoryPojo);
        }
        return inventoryPojoList;
    }

    public void updateInventory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryService.update(inventoryPojo);
        }
    }
    public void addOrderItems(List<OrderItemPojo> addedOrderItems) throws ApiException {
        for (OrderItemPojo orderItemPojo : addedOrderItems) {
            orderItemService.add(orderItemPojo);
        }
    }
    public void deleteInventory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryService.update(inventoryPojo);
        }
    }
    public void updateOrderItems(List<OrderItemPojo> updatedOrderItems,
                                 Map<String, OrderItemPojo> barcodeToOrderItemMapping) throws ApiException {
        for (OrderItemPojo orderItemPojo : updatedOrderItems) {
            int newQuantity = barcodeToOrderItemMapping.get(orderItemPojo.getBarcode()).getQuantity();
            orderItemPojo.setQuantity(newQuantity);
            orderItemService.updateOrderItem(orderItemPojo);
        }
    }
    public void deleteOrderItems(List<OrderItemPojo> deletedOrderItems) throws ApiException {
        for (OrderItemPojo orderItemPojo : deletedOrderItems) {
            orderItemService.delete(orderItemPojo.getId());
        }
    }

    public List<ProductPojo> getProductList(List<String> barcode) throws ApiException {
        List<ProductPojo> productPojoList = new ArrayList<>();
        for (String orderItemBarcode : barcode) {
            ProductPojo productPojo = productService.getAndCheckProductByBarcode(orderItemBarcode);
            productPojoList.add(productPojo);
        }
        return productPojoList;
    }
    public List<OrderItemPojo> getOrderItemsById(int id) throws ApiException {
        return orderItemService.getOrderItemsById(id);
    }
    public List<OrderPojo> getOrderByBarcode (List<InventoryPojo> list ) {
        System.out.println("anknanana");
        List<OrderPojo> list2 = new ArrayList<OrderPojo>();
        list.forEach((temp) -> {
            System.out.println("ppppppppppppp");
            System.out.println(temp.getBarcode());
            OrderPojo p =dao.select(temp.getBarcode());
            list2.add(p);
        });

        return list2;
    }



    @Transactional
    public OrderPojo checkOrderExist(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exit, id: " + id);
        }
        return p;
    }

    public Boolean checkOrderExists(String barcode) throws ApiException {
        OrderPojo p = dao.select(barcode);
        if(p == null) {
            throw new ApiException(("barcode doesn't exist in orders"));
        }
        return true;

    }

    public Boolean nameExist(String name) throws ApiException {
        OrderPojo p = dao.checkName(name);
        if(p == null) {
            return false;
        }
        else {
            throw new ApiException(("brand name+brand category +order name already exist"));
        }
    }
    public LocalDateTime convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay();
    }


    public List<OrderPojo> getOrdersBetweenTime(Date start, Date end){
        LocalDateTime s = convertToLocalDateViaInstant(start);
        LocalDateTime e = convertToLocalDateViaInstant(end);
        return dao.getOrdersForReport(s, e);
    }





    protected static void normalize(OrderPojo p) {
//        p.setBa(StringUtil.toLowerCase(p.get()));
    }



}
