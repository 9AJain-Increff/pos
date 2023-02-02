package com.increff.pos.dto;


import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
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
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.stream.Collectors;

import static com.increff.pos.util.ConversionUtil.convertToInventoryData;
import static com.increff.pos.util.ConversionUtil.convertToOrderData;

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
    }

    @Test
    @Rollback
    public void testGetInventoryItemByIdForInvalidBarcodeThrowsException() throws ApiException {
        exceptionRule.expect(ApiException.class);
        String barcode = "invalid_barcode_101";

        exceptionRule.expectMessage("Product with given barcode does not exist");
        inventoryDto.getInventoryByBarcode(barcode);
    }

    @Test
    @Rollback
    public void testGetInventoryItemByIdForValidBarcodeReturnsData() throws ApiException {
        String barcode = "a1001";
        InventoryData actual = inventoryDto.getInventoryByBarcode(barcode);
        InventoryData expected = new InventoryData(1, barcode, "iphone x", 10);
        AssertUtil.assertEqualInventoryData(expected, actual);
    }

    @Test
    @Rollback
    public void testUpdateInventoryItem() throws ApiException {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setQuantity(100);
        inventoryForm.setBarcode("a1001");
        inventoryDto.updateInventory(inventoryForm);

        Integer productId = products
                .stream()
                .filter(it -> it.getBarcode().equals("a1001"))
                .collect(Collectors.toList()).get(0)
                .getId();

        InventoryPojo inventory = inventoryService.getAndCheckInventoryByProductId(productId);
        Assert.assertEquals(100, inventory.getQuantity());
    }

    @Test
    @Rollback
    public void testUpdateInventoryItemWithNegativeQuantityThrowsException() throws ApiException {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setQuantity(-1);
        inventoryForm.setBarcode("a1001");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("quantity cannot be negative");
        inventoryDto.updateInventory(inventoryForm);
    }

    @Test
    @Rollback
    public void testGetAllInventoryData() throws ApiException {
        List<InventoryData> expected = MockUtil.getMockInventoryData();
        List<InventoryData> actual = inventoryDto.getAllInventory();
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualInventoryData);
    }

    @Test
    @Rollback
    public void testGetInventoryById() throws ApiException {
        ProductPojo product = products
                .stream()
                .filter(it -> it.getBarcode().equals("a1001"))
                .collect(Collectors.toList()).get(0);

        InventoryPojo expected = inventoryService.getAndCheckInventoryByProductId(product.getId());
        InventoryData actual = inventoryDto.getInventoryByProductId(product.getId());
        InventoryData expectedInventoryData =  convertToInventoryData(expected,product);
        AssertUtil.assertEqualInventoryData(expectedInventoryData, actual);
    }

    //can be removed

//    @Test
//    @Rollback
//    public void testAddInventory() throws ApiException {
//        InventoryForm inventoryForm = new InventoryForm();
//        inventoryForm.setQuantity(1);
//        inventoryForm.setBarcode("a1001");
//        InventoryData actual = inventoryDto.addInventory(inventoryForm);
//        InventoryPojo expected = inventoryService.getAndCheckInventoryByProductId(1);
//        ProductPojo product = productService.getProductById(1);
//        InventoryData expectedInventoryData = convertToInventoryData(expected,product);
//        AssertUtil.assertEqualInventoryData(actual, expectedInventoryData);
//    }

}
