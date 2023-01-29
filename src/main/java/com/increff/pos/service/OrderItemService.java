package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void addOrderItem(List<OrderItemPojo> p) throws ApiException {
        for (OrderItemPojo orderItemPojo : p) {
            // TODO: 29/01/23 what is the use of this check? If yes, how are you checking?
            OrderItemPojo check = dao.checkOrderItemExists(orderItemPojo.getProductId());
            dao.insert(orderItemPojo);
        }

    }

    @Transactional
    public void delete(int id) {
        // TODO: throw ApiException
        // TODO: 29/01/23 why?
        dao.delete(id);
    }


    // TODO: 29/01/23 why apiException?
    public List<OrderItemPojo> getOrderItemsById(int id)  {
        return dao.select(id);
    }

    @Transactional
    public List<OrderItemPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void updateOrderItem( OrderItemPojo p) throws ApiException {

        // TODO: 29/01/23 remove system.out.println
        OrderItemPojo ex = getOrderItem(p.getId());
        System.out.println(p.getQuantity());
        ex.setSellingPrice(p.getSellingPrice());
        ex.setQuantity(p.getQuantity());

    }

    // TODO: 29/01/23 why apiException?
    public OrderItemPojo getOrderItem(int id) throws ApiException {
        OrderItemPojo p = dao.getOrderItem(id);

        return p;
    }



    public List<OrderItemPojo> getOrderItemByOrders(List<OrderPojo> orders) throws ApiException {
        List<OrderItemPojo> orderItems = new ArrayList<>();
        for(OrderPojo order : orders){
            List<OrderItemPojo> o = getOrderItemsById(order.getId());
            orderItems.addAll(o);
        }
        return orderItems;
    }



}
