package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductserviceTest extends AbstractUnitTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDao productDao;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void getByIdForInvalidIdThrowsException() throws ApiException {
        exceptionRule.expect(ApiException.class);
        int invalidId = -1;
        exceptionRule.expectMessage("Product with given id does not exist");
        productService.checkProduct(invalidId);
    }

    @Test
    public void checkByInvalidIdThrowsException() throws ApiException {
        exceptionRule.expect(ApiException.class);
        ProductPojo product = MockUtil.getMockProduct();
        product.setId(-1);
        Integer id = product.getId();
        exceptionRule.expectMessage("Product with given id does not exit, id: " + id);
        productService.checkProductByIdAndBarcode(product.getId(),product.getBarcode());
    }

//    @Test
//    public void getByIdForValidIdReturnProductEntity() throws ApiException {
//        ProductPojo expected = MockUtil.getMockProduct();
//        productDao.add(expected);
//        ProductPojo actual = productService.checkProduct(expected.getId());
//        AssertUtil.assertEqualProducts(expected, actual);
//    }

    private List<ProductPojo> insertMockProducts(Integer size) {
        List<ProductPojo> mockProducts = MockUtil.getMockProducts(size);
        mockProducts.forEach(productDao::add);
        return mockProducts;
    }
    @Test
    public void getProductsByBarcodes() {
        List<ProductPojo> expected = insertMockProducts(4);
        List<String> barcodes = new ArrayList<>();
        for(ProductPojo product : expected){
            barcodes.add(product.getBarcode());
        }
        List<ProductPojo> actual = productService.getProductsByBarcodes(barcodes);
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualProducts);
    }

    @Test
    public void getAllReturnsAllProducts() {
        List<ProductPojo> expected = insertMockProducts(4);
        List<ProductPojo> actual = productService.getAllProduct();
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualProducts);
    }

    @Test
    public void getProductByIds()  {
        List<ProductPojo> products = insertMockProducts(5);
        List<Integer> ids = Arrays.asList(products.get(1).getId(), products.get(4).getId(), products.get(2).getId());

        List<ProductPojo> expected = products
                .stream()
                .filter(product -> ids.contains(product.getId()))
                .sorted(Comparator.comparing(ProductPojo::getId))
                .collect(Collectors.toList());

        List<ProductPojo> actual = productService.getProductsByIds(ids)
                .stream()
                .sorted(Comparator.comparing(ProductPojo::getId))
                .collect(Collectors.toList());

        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualProducts);
    }

    @Test
    public void getByBarcodeForValidBarcodeReturnsProduct() throws ApiException {
        ProductPojo mock = MockUtil.getMockProduct();
        productDao.add(mock);

        String barcode = mock.getBarcode();
        ProductPojo actual = productService.getProductByBarcode(barcode);

        AssertUtil.assertEqualProducts(mock, actual);
    }

    @Test
    public void getByBarcodeForInvalidBarcodeThrowsException() throws ApiException {
        String invalidBarcode = "Invalid Barcode";
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given barcode does not exist");
        productService.getProductByBarcode(invalidBarcode);
    }

    @Test
    public void validateSellingPriceThrowsException() throws ApiException {
        Float sellingPrice = 900.8f;
        Float price = 600.8f;
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("selling price should not be more than the MRP = "+price);
        productService.validateSellingPrice(sellingPrice,price);
    }

    @Test
    public void addValidProductInsertsEntity() throws ApiException {
        ProductPojo product = MockUtil.getMockProduct();
        productService.addProduct(product);

        Integer id = product.getId();
        ProductPojo actual = productDao.getProductById(id);

        AssertUtil.assertEqualProducts(product, actual);
    }

    @Test
    public void addDuplicateProductThrowsException() throws ApiException {
        ProductPojo original = MockUtil.getMockProduct();
        productDao.add(original);

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("barcode already exist ");
        ProductPojo duplicate = MockUtil.getMockProduct();
        productService.addProduct(duplicate);
    }

    @Test
    public void updateExistingProductsModifiesTheEntity() throws ApiException {
        ProductPojo product = MockUtil.getMockProduct();
        productDao.add(product);

        product.setPrice(10000.0f);
        productService.update(product);

        ProductPojo actual = productDao.getProductById(product.getId());
        AssertUtil.assertEqualProducts(product, actual);
    }
//    @Test
//    public void updateProductTestChangingBarcodeThrowsException() throws ApiException {
//        ProductPojo product = MockUtil.getMockProduct();
//        productDao.add(product);
//
//        exceptionRule.expect(ApiException.class);
//        exceptionRule.expectMessage("Barcode can't be changed!");
//
//        ProductPojo updateProduct = new Product();
//        updateProduct.setBarcode("New Barcode");
//        updateProduct.setId(product.getId());
//        productService.update(updateProduct);
//    }

    @Test
    public void updateProductTestForNotExistingProductThrowsException() throws ApiException {
        ProductPojo product = MockUtil.getMockProduct();
        int invalidId = -1;
        product.setId(invalidId);

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given id does not exist");

        product.setName("New Name");
        productService.update(product);
    }


}
