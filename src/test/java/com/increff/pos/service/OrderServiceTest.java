package com.increff.pos.service;

import com.increff.pos.dao.AbstractDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.MockUtil.getNewOrder;
import static org.junit.Assert.assertEquals;

public class OrderServiceTest extends AbstractUnitTest {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Test
    public void createOrderThatDoesNotExists() throws ApiException {
        OrderPojo order = getNewOrder();
        LocalDateTime currentTime = order.getCreatedOn();
        orderService.createNewOrder(order);

        OrderPojo createdOrder = orderDao.getOrderById(order.getId());
        assertEquals(currentTime, createdOrder.getCreatedOn());
    }
    @Test
    public void getOrderBetweenDate() throws ApiException {
        List<OrderPojo> expected = new ArrayList<>();
        OrderPojo order = getNewOrder();
        expected.add(order);
        orderService.addOrder(order);
        LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime yesterday = today.minusDays(1);
        List<OrderPojo> actual = orderService.getOrdersBetweenTime(yesterday,today);
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualOrders);
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
//
//    @Test(expected = ApiException.class)
//    public void updateOrderWithInvalidIdThrowsApiException() throws ApiException {
//        // insert
//        OrderPojo order = getNewOrder();
//        orderDao.insert(order);
//
//        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC).plusHours(15);
//        order.setCreatedOn(time);
//        order.setId(699);
//        orderService.updateOrder(order);
//    }

    @Test
    public void getAll() {
        List<OrderPojo> actualList = orderService.getAllOrders();
        List<OrderPojo> expectedList = orderDao.selectAll();

        assertEquals(expectedList.size(), actualList.size());

        for (int i = 0; i < expectedList.size(); i++) {
            OrderPojo expected = expectedList.get(i);
            OrderPojo actual = actualList.get(i);
            assertEquals(expected, actual);
        }
    }

//    @Test(expected = ApiException.class)
//    public void getForInvalidIdThrowsApiException() throws ApiException {
//        exceptionRule.expect(ApiException.class);
//        exceptionRule.expectMessage("Order with given id does not exist");
//        orderService.getOrderByOrderId(-1);
//    }

    @Test
    public void getOrderForValidIdReturnsOrderPojo() throws ApiException {
        OrderPojo order = getNewOrder();
        orderDao.insert(order);

        OrderPojo expected = orderDao.selectOrderById(order.getId());
        OrderPojo actual = orderService.getOrderByOrderId(order.getId());

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCreatedOn(), actual.getCreatedOn());
    }

}
