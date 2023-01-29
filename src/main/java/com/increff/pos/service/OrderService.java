package com.increff.pos.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.pos.util.ConversionUtil.convertToOrderPojo;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo addOrder(OrderPojo p) throws ApiException {
        return orderDao.insert(p);
    }
//    @Transactional
//    public void delete(int id) {
//        dao.delete(id);
//    }




    @Transactional
    public List<OrderPojo> getAllOrders() {
        return orderDao.selectAll();
    }

    // TODO: 29/01/23 what is the diff between addOrder and  createNewOrder?
    public OrderPojo createNewOrder() throws ApiException {
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);
        // TODO: 29/01/23 why convert is there in service? It should be there in Dto
        OrderPojo newOrder = convertToOrderPojo(date);
        return orderDao.insert(newOrder);
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void addPdfURL(Integer id){
        OrderPojo order = orderDao.getOrderById(id);
        // TODO: 29/01/23 move this to properties file
        order.setOrderURL("/home/ankurjain/Downloads/employee-spring-full-master/order"+id+".pdf");
    }


    // TODO: 29/01/23 remove apiexception if not used
    public String getPdfUrl(Integer id) throws ApiException {
        OrderPojo order = orderDao.getOrderById(id);
        return order.getOrderURL();
    }



    @Transactional
    public OrderPojo checkOrderExist(int id) throws ApiException {
        OrderPojo p = orderDao.select(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exit, id: " + id);
        }
        return p;
    }

    // TODO: 29/01/23 move to some helper class
    public LocalDateTime convertToLocalDateViaInstant(Date dateToConvert) {

        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay();
    }


    public List<OrderPojo> getOrdersBetweenTime(Date start, Date end){
        // TODO: 29/01/23 rename s,e
        LocalDateTime s,e;
        if(start == null) {
            s = LocalDateTime.MIN;
        }
        else{
            s = convertToLocalDateViaInstant(start);
        }
        if(end == null){
            e = LocalDateTime.now(ZoneOffset.UTC);
        }
        else {
            e = convertToLocalDateViaInstant(end);
        }
        return orderDao.getOrdersForReport(s, e);
    }








}
