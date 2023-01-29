package com.increff.pos.service;

import com.increff.pos.dao.AbstractDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.util.MockUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.increff.pos.util.MockUtil.getNewOrder;
import static org.junit.Assert.assertEquals;

public class OrderServiceTest extends AbstractUnitTest {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;

    @Test
    public void createOrderThatDoesNotExists() throws ApiException {
        OrderPojo order = getNewOrder();
        LocalDateTime currentTime = order.getCreatedOn();
        orderService.createNewOrder(order);

        OrderPojo createdOrder = orderDao.getOrderById(order.getId());
        assertEquals(currentTime, createdOrder.getCreatedOn());
    }

//    @Test(expected = ApiException.class)
//    public void createOrderThatAlreadyExistsThrowsException() throws ApiException {
//        OrderPojo order = getNewOrder();
//        orderDao.insert(order);
//        orderService.create(order);
//    }


    @Test
    @Rollback
    public void updateOrder() throws ApiException {
        // Insert
        OrderPojo order = getNewOrder();
        orderDao.insert(order);

        // Update
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC).plusHours(15);
        order.setCreatedOn(time);
        orderService.updateOrder(order);

        // Check
        OrderPojo actualOrder = orderDao.getOrderById(order.getId());

        assertEquals(time, actualOrder.getCreatedOn());
        assertEquals(order.getId(), actualOrder.getId());
    }

    @Test(expected = ApiException.class)
    public void updateOrderWithInvalidIdThrowsApiException() throws ApiException {
        // insert
        OrderPojo order = getNewOrder();
        orderDao.insert(order);

        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC).plusHours(15);
        order.setCreatedOn(time);
        order.setId(699);
        orderService.updateOrder(order);
    }


}
