//package com.increff.pos.dto;
//
//
//import com.increff.pos.pojo.BrandPojo;
//import com.increff.pos.pojo.ProductPojo;
//import com.increff.pos.service.*;
//import com.increff.pos.util.Mock;
//import org.junit.Before;
//import org.junit.Assert;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class InventoryDtoTest extends AbstractUnitTest {
//
//    @Autowired
//    private InventoryDto inventoryApiDto;
//
//    @Autowired
//    private InventoryService inventoryService;
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private BrandService brandService;
//
//    @Rule
//    public ExpectedException exceptionRule = ExpectedException.none();
//
//    private List<ProductPojo> products;
//
//    @Before
//    public void setUp() {
//        List<BrandPojo> brands = Mock.setUpBrands(brandService);
//        products = MockUtils.setUpProductsAndInventory(brands, productService, inventoryService);
//    }
//
//    @Test
//    public void testGetInventoryItemByIdForInvalidBarcodeThrowsException() throws ApiException {
//        exceptionRule.expect(ApiException.class);
//        String barcode = "INVALID_BARCODE_101";
//
//        exceptionRule.expectMessage("Can't find any product with barcode: " + barcode);
//        inventoryApiDto.getByBarcode(barcode);
//    }
//
//    @Test
//    public void testGetInventoryItemByIdForValidBarcodeReturnsData() throws ApiException {
//        String barcode = "a1001";
//        InventoryData actual = inventoryApiDto.getByBarcode(barcode);
//        InventoryData expected = new InventoryData(barcode, "iphone x", 10);
//        AssertUtils.assertEqualInventoryData(expected, actual);
//    }
//
//    @Test
//    public void testUpdateInventoryItem() throws ApiException {
//        InventoryForm inventoryForm = new InventoryForm();
//        inventoryForm.setQuantity(100);
//        inventoryApiDto.update("a1001", inventoryForm);
//
//        Integer productId = products
//                .stream()
//                .filter(it -> it.getBarcode().equals("a1001"))
//                .collect(Collectors.toList()).get(0)
//                .getId();
//
//        Inventory inventory = inventoryService.get(productId);
//        Assert.assertEquals(Integer.valueOf(100), inventory.getQuantity());
//    }
//
//    @Test
//    public void testUpdateInventoryItemWithNegativeQuantityThrowsException() throws ApiException {
//        exceptionRule.expect(ApiException.class);
//        exceptionRule.expectMessage("Invalid input: 'quantity' should not be a negative number!");
//        inventoryApiDto.update("a1001", new InventoryForm(-1));
//    }
//
//    @Test
//    public void testGetAllInventoryData() {
//        List<InventoryData> expected = MockUtils.getMockInventoryData();
//        List<InventoryData> actual = inventoryApiDto.getAll();
//        AssertUtils.assertEqualList(expected, actual, AssertUtils::assertEqualInventoryData);
//    }
//
//}
