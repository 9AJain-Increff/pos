package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import java.util.List;
import static com.increff.pos.util.Normalization.normalize;
public class BrandServiceTest extends AbstractUnitTest{
    @Autowired
    private BrandDao brandDao;

    @Autowired
    private BrandService brandService;

    private BrandPojo addMockBrand() throws ApiException {
        BrandPojo mockBrand = MockUtil.getMockBrand();
        brandDao.addBrand(mockBrand);
        return mockBrand;
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        MockUtil.setUpBrands(brandService);
    }

    @Test
    @Rollback
    public void getBrandByIdForUnknownIdThrowsApiException() throws ApiException {
        exceptionRule.expect(ApiException.class);
        int id = -1;
        exceptionRule.expectMessage("Brand witFh given ID does not exit, id: " + id);
        brandService.getAndCheckBrandById(id);
    }
    @Test
    @Rollback
    public void getBrandByIdForValidIdReturnsBrand() throws ApiException {
        BrandPojo brand = MockUtil.getMockBrand();
        brandDao.addBrand(brand);

        BrandPojo actual = brandService.getAndCheckBrandById(brand.getId());
        AssertUtil.assertEqualBrands(brand, actual);
    }

    @Test
    @Rollback
    public void getAll() {
        List<BrandPojo> actual = brandService.getAllBrand();
        List<BrandPojo> expected = MockUtil.BRANDS;
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualBrands);
    }
    @Test
    @Rollback
    public void addValidBrandReturnsAddedPojo() throws ApiException {
        BrandPojo brand = MockUtil.getMockBrand();
        BrandPojo actual = brandService.addBrand(brand);
        BrandPojo expected = brandDao.getBrandById(brand.getId());
        AssertUtil.assertEqualBrands(expected, actual);
    }
    @Test
    @Rollback
    public void addingDuplicateBrandThrowsApiException() throws ApiException {
        BrandPojo brand = MockUtil.getMockBrand();
        normalization(brand);
        brandDao.addBrand(brand);

        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand with given name and category already exist");

        BrandPojo duplicateBrand = MockUtil.getMockBrand();
        brandService.addBrand(duplicateBrand);
    }

    @Test
    @Rollback
    public void update() throws ApiException {
        BrandPojo brand = addMockBrand();
        int id = brand.getId();

        brand.setName("updated name");
        brand.setCategory("updated category");

        BrandPojo actual = brandService.update(id,brand);
        BrandPojo expected = brandDao.getBrandById(id);

        AssertUtil.assertEqualBrands(expected, actual);
    }

    @Test
    @Rollback
    public void testSelectByNameAndCategory() throws ApiException {
        BrandPojo expected = addMockBrand();
        BrandPojo actual = brandService.getBrandByNameAndCategory(expected.getName(), expected.getCategory());
        AssertUtil.assertEqualBrands(expected, actual);
    }

    @Test
    @Rollback
    public void selectByNameAndCategoryForUnknownAttrThrowsException() throws ApiException {
        String name = "Unknown-Brand";
        String category = "Unknown-Category";

        exceptionRule.expect(ApiException.class);

        String message = "Brand with given name and category does not exit ";
        exceptionRule.expectMessage(message);

        brandService.checkBrandExistByNameAndCategory(name, category);
    }
    @Test
    @Rollback
    public void duplicateCheckForDuplicateBrandsThrowsException() throws ApiException {
        BrandPojo brand = addMockBrand();
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Brand with given name and category already exist");
        brandService.addBrand(brand);
    }


    private void normalization(BrandPojo brand){
        brand.setName(normalize(brand.getName()));
        brand.setCategory(normalize(brand.getCategory()));
    }

}
