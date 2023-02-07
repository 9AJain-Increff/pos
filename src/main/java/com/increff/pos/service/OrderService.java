package com.increff.pos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Value("${pdfUrl}")
    private String pdfUrl;

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo addOrder(OrderPojo p) {
        return orderDao.insert(p);
    }

    public OrderPojo getOrderByOrderId(Integer orderId) throws ApiException {
        OrderPojo order = orderDao.selectOrderById(orderId);
        if (order == null) {
            throw new ApiException("Order with given id does not exist");
        }
        return order;
    }


    public List<OrderPojo> getAllOrders() {
        return orderDao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo createNewOrder(OrderPojo newOrder) {
        return orderDao.insert(newOrder);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo updateOrder(OrderPojo order) throws ApiException {
        OrderPojo o = orderDao.getOrderById(order.getId());
        if (o == null)
            throw new ApiException("order id doesn't exist");
        o.setCreatedOn(order.getCreatedOn());
        o.setOrderURL(order.getOrderURL());
        return o;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addPdfURL(Integer id) {
        OrderPojo order = orderDao.getOrderById(id);

        order.setOrderURL(pdfUrl + id + ".pdf");
    }




    public List<OrderPojo> getOrdersBetweenTime(LocalDateTime start, LocalDateTime end) {
        return orderDao.selectAllByDates(start,end);
    }


}
