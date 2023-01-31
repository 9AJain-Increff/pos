package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryServiceTest extends AbstractUnitTest {

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InventoryDao inventoryDao;


    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
    }

    @Test
    public void addInventory() throws ApiException {
        InventoryPojo expected = MockUtil.getMockInventory(1);
        inventoryService.addInventory(expected);
        InventoryPojo actual = inventoryDao.selectInventoryByProductId(1);
        AssertUtil.assertEqualInventory(expected, actual);
    }

    //    @Test
//    public void addingDuplicateInventoryThrowsException() throws ApiException {
//        int productId = 1;
//        InventoryPojo original = MockUtil.getMockInventory(productId);
//        inventoryService.addInventory(original);
//
//        exceptionRule.expect(ApiException.class);
//        exceptionRule.expectMessage("Inventory for product id: " + productId + " already exists!");
//        InventoryPojo duplicate = MockUtil.getMockInventory(productId);
//        inventoryService.addInventory(duplicate);
//    }
    @Test
    public void updatingNonExistingInventoryThrowsException() throws ApiException {
        int invalidId = -1;
        InventoryPojo mock = MockUtil.getMockInventory(invalidId);
        mock.setQuantity(100);

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Inventory Doesn't exist");
        inventoryService.update(mock);
    }


    @Test
    public void updateInventory() throws ApiException {
        InventoryPojo mock = MockUtil.getMockInventory(1);
        mock.setQuantity(10);

        inventoryDao.insert(mock);

        mock.setQuantity(100);
        inventoryService.update(mock);

        InventoryPojo actual = inventoryDao.selectInventoryByProductId(1);
        AssertUtil.assertEqualInventory(mock, actual);
    }

    @Test
    @Rollback
    public void testForGetAndCheckInventoryByProductId() throws ApiException {
        InventoryPojo expected = MockUtil.getMockInventory(1);
        inventoryDao.insert(expected);
        InventoryPojo actual = inventoryService.getAndCheckInventoryByProductId(1);
        AssertUtil.assertEqualInventory(expected, actual);
    }

    @Test
    @Rollback
    public void testForGetAllInventory() {
        List<InventoryPojo> expected = new ArrayList<>();
        expected.add(MockUtil.getMockInventory(1));
        for (InventoryPojo inventory : expected)
            inventoryDao.insert(inventory);
        List<InventoryPojo> actual = inventoryService.getAllInventory();
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualInventory);
    }

//    @Test
//    public void updateInventoryWithInsufficientStock() throws ApiException {
//        Integer productId = 1;
//        ProductPojo product = MockUtil.getMockProduct();
//        product.setId(productId);
//        InventoryPojo inventory = MockUtil.getMockInventory(productId);
//        inventoryDao.insert(inventory);
//
//        exceptionRule.expect(ApiException.class);
//        exceptionRule.expectMessage("Insufficient inventory");
//
//        inventoryService.update(
//                Collections.singletonList(product),
//                Collections.singletonList(100)
//        );
//    }

}
