package com.increff.employee.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.OrderItemDao;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.util.StringUtil;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderItemPojo p) throws ApiException {
        normalize(p);

        System.out.println("ankur jainnnnnnnnnnn");
        OrderItemPojo check = dao.checkOrderItemExists(p.getBarcode());

        dao.insert(p);


    }

    @Transactional
    public void delete(int id) {
        // TODO: throw ApiException
        dao.delete(id);
    }


    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemPojo> getOrderItemsById(int id) throws ApiException {
        return dao.select(id);
    }

    @Transactional
    public List<OrderItemPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void updateOrderItem( OrderItemPojo p) throws ApiException {
        normalize(p);
        OrderItemPojo ex = getOrderItem(p.getId());
        System.out.println(p.getQuantity());
//        System.out.println(p.getName());
        ex.setQuantity(p.getQuantity());
//        ex.set(p.getPrice());
//        else{
//            throw new ApiException("product name + category already exists");
//        }

//        dao.update(ex);
    }


    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, List<OrderItemPojo> p) throws ApiException {

//        List<OrderPojo> list2 = new ArrayList<OrderPojo>();
//        list.forEach((temp) -> {
//            System.out.println("ppppppppppppp");
//            System.out.println(temp.getBarcode());
//            OrderPojo p =dao.select(temp.getBarcode());
//            list2.add(p);
//        });
//        normalize(p);
//        OrderItemPojo ex = getOrderItem(id);
////        System.out.println(p.getName());
//        ex.setQuantity(p.getQuantity());
//        ex.setPrice(p.getPrice());
//
////        else{
////            throw new ApiException("orderItem name + category already exists");
////        }
//
//        dao.update(ex);
    }


    public List<OrderItemPojo> getOrderItemByBarcode(List<InventoryPojo> list) {
        System.out.println("anknanana");
        List<OrderItemPojo> list2 = new ArrayList<OrderItemPojo>();
        list.forEach((temp) -> {
            System.out.println("ppppppppppppp");
            System.out.println(temp.getBarcode());
            OrderItemPojo p = dao.select(temp.getBarcode());
            list2.add(p);
        });

        return list2;
    }


    public OrderItemPojo getOrderItem(int id) throws ApiException {
        OrderItemPojo p = dao.orderItem(id);

        return p;
    }

    public OrderItemPojo getOrderItem(int orderId, String barcode) throws ApiException {
        OrderItemPojo p = dao.orderItem(orderId, barcode);

        return p;
    }



//    public Boolean checkOrderItemExists(String barcode) throws ApiException {
//        OrderItemPojo p = dao.select(barcode);
//        if(p == null) {
//            throw new ApiException(("barcode doesn't exist in orderItems"));
//        }
//        return true;
//
//    }

//    @Transactional
//    public OrderItemPojo getPrice(String barcode) throws ApiException {
//        OrderItemPojo p = dao.select(barcode);
//        return p;
//    }

//    public Boolean nameExist(String name) throws ApiException {
//        OrderItemPojo p = dao.checkName(name);
//        if(p == null) {
//            return false;
//        }
//        else {
//            throw new ApiException(("brand name+brand category +orderItem name already exist"));
//        }
//    }


//    public Boolean     checkBarcodeSameOrNot(String updatedBarcode,int id) throws ApiException {
//        OrderItemPojo p = dao.select(id);
//        String currentBarcode = p.getBarcode();
//
//        if(updatedBarcode == currentBarcode) {
//            return true;
//        }
//        return false;
//
//    }


    protected static void normalize(OrderItemPojo p) {
        p.setBarcode(StringUtil.toLowerCase(p.getBarcode()));
    }


}
