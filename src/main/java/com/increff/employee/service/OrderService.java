package com.increff.employee.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

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


    public List<OrderPojo> getOrdersBetweenTime(LocalDateTime start, LocalDateTime end){
        return dao.getOrdersForReport(start, end);
    }





    protected static void normalize(OrderPojo p) {
//        p.setBa(StringUtil.toLowerCase(p.get()));
    }



}
