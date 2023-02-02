package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.pos.util.Normalizationutil.normalizeOrderItem;


@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void addOrderItem(List<OrderItemPojo> p) {
        for (OrderItemPojo orderItemPojo : p) {
            normalizeOrderItem(orderItemPojo);
        }
        for (OrderItemPojo orderItemPojo : p) {
            dao.insert(orderItemPojo);
        }

    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(Integer orderItemId) {
        dao.delete(orderItemId);
    }

    public List<OrderItemPojo> getOrderItemsById(Integer id) {
        return dao.select(id);
    }

    public List<OrderItemPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateOrderItem(OrderItemPojo p) {

        OrderItemPojo ex = getOrderItem(p.getId());
        ex.setSellingPrice(p.getSellingPrice());
        ex.setQuantity(p.getQuantity());

    }

    public OrderItemPojo getOrderItem(Integer id) {
        OrderItemPojo p = dao.getOrderItem(id);

        return p;
    }


    public List<OrderItemPojo> getOrderItemByOrders(List<OrderPojo> orders) {
        List<OrderItemPojo> orderItems = new ArrayList<>();
        for (OrderPojo order : orders) {
            List<OrderItemPojo> o = getOrderItemsById(order.getId());
            orderItems.addAll(o);
        }
        return orderItems;
    }


}
