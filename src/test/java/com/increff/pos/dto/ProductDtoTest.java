package com.increff.pos.dto;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.exception.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.increff.pos.util.ConversionUtil.convertToProductData;

public class ProductDtoTest extends AbstractUnitTest {
    @Autowired
    private ProductDto productDto;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private List<ProductData> productDataList;

    @Before
    public void setUp() {
        List<BrandPojo> mockBrands = MockUtil.setUpBrands(brandService);
        List<ProductPojo> mockProducts = MockUtil.setupProducts(mockBrands, productService);
        productDataList = MockUtil.getMockProductDataList(mockBrands, mockProducts);
    }

    @Test
    public void addProductWithUnknownBrandThrowsException() throws ApiException {
        ProductForm productForm = new ProductForm();
        productForm.setBrandName("INVALID_BRAND");
        productForm.setBrandCategory("INVALID_CATEGORY");
        productForm.setName("Product Name");
        productForm.setPrice(1000.0034f);
        productForm.setBarcode("aBcd134");

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand with given name and category does not exit ");
        ProductData expectedProductData = productDto.addProduct(productForm);
        ProductPojo actual = productService.getProductByBarcode(expectedProductData.getBarcode());
        BrandPojo brand = brandService.getAndCheckBrandById(actual.getBrandId());
        ProductData actualProductData = convertToProductData(actual, brand);
        AssertUtil.assertEqualProductData(expectedProductData, actualProductData);
    }

    @Test
    public void addProductWithBlankNameThrowsApiException() throws ApiException {
        ProductForm productForm = MockUtil.getMockProductForm();
        productForm.setName("   ");

        exceptionRule.expect(ApiException.class);
        String message = "name cannot be empty";
        exceptionRule.expectMessage(message);
        productDto.addProduct(productForm);
    }

    @Test
    public void addProductWithBlankBarcodeThrowsApiException() throws ApiException {
        ProductForm productForm = MockUtil.getMockProductForm();
        productForm.setBarcode(null);

        exceptionRule.expect(ApiException.class);
        String message = "barcode cannot be empty";
        exceptionRule.expectMessage(message);
        productDto.addProduct(productForm);
    }

    @Test
    public void addProductWithBlankBrandNameThrowsApiException() throws ApiException {
        ProductForm productForm = MockUtil.getMockProductForm();
        productForm.setBrandName("   ");

        exceptionRule.expect(ApiException.class);
        String message = "brand cannot be empty";
        exceptionRule.expectMessage(message);

        productDto.addProduct(productForm);
    }

    @Test
    public void addProductWithBlankCategoryThrowsApiException() throws ApiException {
        ProductForm productForm = MockUtil.getMockProductForm();
        productForm.setBrandCategory("   ");

        exceptionRule.expect(ApiException.class);
        String message = "category cannot be empty";
        exceptionRule.expectMessage(message);
        productDto.addProduct(productForm);
    }

    @Test
    public void addProductWithNegativePriceThrowsApiException() throws ApiException {
        ProductForm productForm = MockUtil.getMockProductForm();
        productForm.setPrice(-10.0f);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Invalid input: price can only be a positive number!");
        productDto.addProduct(productForm);
    }

    @Test
    public void addingProductWithZeroPriceThrowsApiException() throws ApiException {
        ProductForm productForm = MockUtil.getMockProductForm();
        productForm.setPrice(0.0f);
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Invalid input: price can only be a positive number!");
        productDto.addProduct(productForm);
    }

    @Test
    public void testAddValidForm() throws ApiException {
        ProductForm productForm = MockUtil.getMockProductForm();
        ProductData expectedProductData = productDto.addProduct(productForm);
        ProductPojo actual = productService.getProductByBarcode(expectedProductData.getBarcode());
        BrandPojo brand = brandService.getAndCheckBrandById(actual.getBrandId());
        ProductData actualProductData = convertToProductData(actual, brand);
        AssertUtil.assertEqualProductData(expectedProductData, actualProductData);
    }

    @Test
    public void getProductByBarcodeForValidBarcodeReturnsPojo() throws ApiException {
        ProductData expected = productDataList.get(0);
        ProductData actual = productDto.getProductByBarcode(expected.getBarcode());
        AssertUtil.assertEqualProductData(expected, actual);
    }

    @Test
    public void getByBarcodeForInvalidBarcodeThrowsException() throws ApiException {
        String barcode = "invalid_barcode";
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Product with given barcode does not exist");
        productDto.getProductByBarcode(barcode);
    }

    @Test
    public void getAllReturnsAllProducts() throws ApiException {
        List<ProductData> actual = productDto.getAllProduct();
        AssertUtil.assertEqualList(productDataList, actual, AssertUtil::assertEqualProductData);
    }

    @Test
    public void testUpdateForInvalidIdThrowsException() throws ApiException {
        ProductForm form = MockUtil.getMockProductForm();
        exceptionRule.expect(ApiException.class);
        int invalidId = -1;
        exceptionRule.expectMessage("Product with given id does not exit, id: " + invalidId);
        productDto.update(invalidId, form);
    }


}