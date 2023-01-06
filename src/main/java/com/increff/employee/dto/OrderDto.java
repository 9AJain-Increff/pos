package com.increff.employee.dto;


import com.increff.employee.model.OrderData;
import com.increff.employee.model.OrderItemData;
import com.increff.employee.model.OrderItemForm;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

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


    public List<OrderItemData> get(int id) throws ApiException {
        System.out.println("ankur jainnnnnnnnnnnnnnnnnnnnnnnnnn");
        OrderPojo b = service.getCheck(id);
        List<OrderItemPojo> p = orderItemService.get(id);
        List<OrderItemData> list2 = new ArrayList<OrderItemData>();
        for (OrderItemPojo pojo : p) {
//            productService.getPrice(pojo.getBarcode());
            list2.add(convertToOrderItemData(pojo));
        }
        return (list2);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addingOrder(List<OrderItemForm> form) throws ApiException {
        System.out.println("ankur jainnnnnnnnnnn");

        for (OrderItemForm temp : form) {
            InventoryPojo inventoryPojo = inventoryService.get(temp.getBarcode());
            if (temp.getQuantity() > inventoryPojo.getQuantity()) {
                throw new ApiException("Maximum available quantity for the barcode" + temp.getBarcode() + " is " + inventoryPojo.getQuantity());
            }
            else{
                inventoryPojo.setQuantity(inventoryPojo.getQuantity()-temp.getQuantity());
                inventoryService.update(temp.getBarcode(),inventoryPojo);
            }
        }
        List<OrderPojo> list = new ArrayList<OrderPojo>();
        List<Integer> orderPrice = new ArrayList<Integer>();
        for (int i = 0; i < form.size(); i++) {
            String barcode = form.get(i).getBarcode();
            productService.checkProductExists(barcode);
        }
        for (int i = 0; i < form.size(); i++) {
            String barcode = form.get(i).getBarcode();
            ProductPojo p = productService.getPrice(barcode);
            orderPrice.add(p.getPrice());
        }
        OrderPojo p = new OrderPojo();
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);
//        System.out.println("Current time of the day using Date - 12 hour format: " + formattedDate);
        p.setCreatedOn(date);
        p = convertToOrderPojo(date);
        OrderPojo updatedOrder = service.add(p);

        int orderId = updatedOrder.getId();

        OrderItemPojo orderItemPojo = new OrderItemPojo();

        for (int i = 0; i < form.size(); i++) {
            OrderItemPojo o = convertToOrderItemPojo(orderPrice.get(i), form.get(i), orderId);
            orderItemService.add(o);
        }

    }

    public void deleting(int id) throws ApiException {
        orderItemService.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updating(int id, List<OrderItemForm> form) throws ApiException {

        if (form.size() == 0) {
            throw new ApiException("Add a Order Item");
        }


        List<OrderItemPojo> p = new ArrayList<OrderItemPojo>();
        for (OrderItemForm temp : form) {
            System.out.println(temp);

            OrderItemPojo p1 = convertToOrderItemPojo(temp.getPrice(), temp, id);
            p.add(p1);
            System.out.println("ankur jainanianianianianiani");
            System.out.println("ankur jainanianianianianiani");
        }



        System.out.println("0");
        Map<Integer, OrderItemPojo> maps = new HashMap<>();
        maps.clear();
        final Integer[] orderId = new Integer[1];
        System.out.println("checking..................."+orderId[0]+ "   "+ id);
        p.forEach((temp) -> {
            orderId[0] = temp.getOrderId();
            maps.put(temp.getId(), temp);

        });
        List<OrderItemPojo> toUpdate = new ArrayList<OrderItemPojo>();
        List<OrderItemPojo> toDelete = new ArrayList<OrderItemPojo>();
        List<OrderItemPojo> toAdd = new ArrayList<OrderItemPojo>();

        List<OrderItemPojo> getData = orderItemService.get(orderId[0]);

        for (OrderItemPojo data : getData) {
            if (maps.get(data.getId()) == null) {
                toDelete.add(data);
            } else {
                toUpdate.add(data);
                maps.remove(data.getId());
            }
        }

        for (OrderItemPojo data : p) {
            toAdd.add(data);
            maps.remove(data.getId());
        }
        System.out.println("3");
        for (OrderItemPojo data : toDelete) {
            orderItemService.delete(data.getId());
        }
        for (OrderItemPojo data : toAdd) {
            InventoryPojo inventoryPojo = inventoryService.get(data.getBarcode());
            OrderItemPojo orderItemPojo = orderItemService.getOrderItem(id, data.getBarcode());
            int extraRequired = data.getQuantity() - orderItemPojo.getQuantity();
            checkAdd(inventoryPojo,extraRequired);
            orderItemService.add(data);
        }
        for (OrderItemPojo data : toUpdate) {
            InventoryPojo inventoryPojo = inventoryService.get(data.getBarcode());
//        OrderItemPojo orderItemPojo = orderItemService.getOrderItem(id, temp.getBarcode());

            int extraRequired = data.getQuantity() ;
            checkAdd(inventoryPojo,extraRequired);
            orderItemService.updateOrderItem(data);
        }


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


    private  void checkAdd(InventoryPojo inventoryPojo, int extraRequired) throws ApiException {
        if (extraRequired > inventoryPojo.getQuantity()) {
            throw new ApiException("Maximum available quantity for the barcode" + inventoryPojo.getBarcode() + " is " + inventoryPojo.getQuantity());
        } else {

            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - extraRequired);
            inventoryService.update(inventoryPojo.getBarcode(), inventoryPojo);

        }
    }
//    private static OrderPojo convert(Date d) {
//        OrderPojo o = new OrderPojo();
//        o.setCreatedOn(d);
//        return o;
//    }
//    private static OrderData convert(OrderPojo p) {
//        OrderData d = new OrderData();
//        d.setId(p.getId());
//        d.setCreatedOn(p.getCreatedOn());
//        return d;
//    }
//
//
//    private static OrderItemData convert(OrderItemPojo p) {
//        OrderItemData d = new OrderItemData();
//        d.setBarcode(p.getBarcode());
//        d.setPrice(p.getPrice());
//        d.setQuantity(p.getQuantity());
//        return d;
//    }
//
////    private static  OrderPojo convert(OrderItemForm f) {
////        OrderPojo p = new OrderPojo();
////        p.setName(f.getName());
////        p.setBrandCategory(f.getBrandCategory());
////        p.setPrice(f.getPrice());
////        p.setBrandName(f.getBarcode());
////        p.setBrandName(f.getBrandName());
////        p.setBarcode(f.getBarcode());
////        return p;
////    }
//
//    private static  OrderItemPojo convert(int price,OrderItemForm o, int id) {
//        OrderItemPojo p = new OrderItemPojo();
//        p.setQuantity(o.getQuantity());
//        p.setOrderId(id);
//        p.setBarcode(o.getBarcode());
//        p.setPrice(price);
//        return p;
//    }

}
