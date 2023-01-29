package com.increff.pos.dto;


import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
//import com.increff.pos.util.MockUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private List<ProductPojo> products;

    @Before
    public void setUp() {
        List<BrandPojo> brands = MockUtil.setUpBrands(brandService);
        products = MockUtil.setUpProductsAndInventory(brands, productService, inventoryService);
        List<InventoryPojo> inventories = inventoryService.getAllInventory();
        System.out.println(inventories);
    }

    @Test
    public void testGetInventoryItemByIdForInvalidBarcodeThrowsException() throws ApiException {
        exceptionRule.expect(ApiException.class);
        String barcode = "invalid_barcode_101";

        exceptionRule.expectMessage("Product with given barcode does not exit, barcode: " + barcode);
        inventoryDto.getInventoryByBarcode(barcode);
    }

    @Test
    public void testGetInventoryItemByIdForValidBarcodeReturnsData() throws ApiException {
        String barcode = "a1001";
        InventoryData actual = inventoryDto.getInventoryByBarcode(barcode);
        InventoryData expected = new InventoryData( 1,barcode, "iphone x", 10);
        AssertUtil.assertEqualInventoryData(expected, actual);
    }

    @Test
    public void testUpdateInventoryItem() throws ApiException {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setQuantity(100);
        inventoryForm.setBarcode("a1001");
        inventoryDto.updateInventory( inventoryForm);

        Integer productId = products
                .stream()
                .filter(it -> it.getBarcode().equals("a1001"))
                .collect(Collectors.toList()).get(0)
                .getId();

        InventoryPojo inventory = inventoryService.getAndCheckInventoryByProductId(productId);
        Assert.assertEquals(100,inventory.getQuantity());
    }

    @Test
    public void testUpdateInventoryItemWithNegativeQuantityThrowsException() throws ApiException {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setQuantity(-1);
        inventoryForm.setBarcode("a1001");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("quantity cannot be negative");
        inventoryDto.updateInventory( inventoryForm);
    }

    @Test
    public void testGetAllInventoryData() throws ApiException {
        List<InventoryData> expected = MockUtil.getMockInventoryData();
        List<InventoryData> actual = inventoryDto.getAllInventory();
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualInventoryData);
    }

}
