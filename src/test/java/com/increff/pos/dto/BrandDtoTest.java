package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.*;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.increff.pos.util.ConversionUtil.convertToBrandData;
import static org.junit.Assert.assertEquals;

public class BrandDtoTest extends AbstractUnitTest {
    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandDto brandDto;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    @Rollback
    public void addBrandTestWithValidInput() throws ApiException {
        BrandForm brandForm = new BrandForm();
        brandForm.setName("nike");
        brandForm.setCategory("shoes");
        BrandData brand = brandDto.addBrand(brandForm);
        assertEquals("nike", brand.getName());
        assertEquals("shoes", brand.getCategory());
    }

    @Test
    @Rollback
    public void addBrandThrowsExceptionForBlankCategory() throws ApiException {
        BrandForm brandForm = new BrandForm();
        brandForm.setName("nike");
        brandForm.setCategory(" ");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("category cannot be empty");
        brandDto.addBrand(brandForm);

    }

    @Test
    @Rollback
    public void addBrandThrowsExceptionForBlankName() throws ApiException {
        BrandForm brandForm = new BrandForm();
        brandForm.setName(" ");
        brandForm.setCategory("shoes");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("name cannot be empty");
        brandDto.addBrand(brandForm);

    }

    @Test
    @Rollback
    public void addBrandThrowsExceptionForBrandAlreadyExist() throws ApiException {
        BrandForm brandForm = new BrandForm();
        brandForm.setName(" ");
        brandForm.setCategory("shoes");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("name cannot be empty");
        brandDto.addBrand(brandForm);

    }

    @Test
    @Rollback
    public void getByValidId() throws ApiException {
        BrandPojo brand = MockUtil.getMockBrand();
        brandService.addBrand(brand);

        int id = brand.getId();
        BrandData actual = brandDto.getBrandById(id);
        BrandData expected = convertToBrandData(brand);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCategory(), actual.getCategory());
    }

    @Test
    @Rollback
    public void getByIdForInvalidIdThrowsApiException() throws ApiException {
        exceptionRule.expect(ApiException.class);
        int id = -1;
        exceptionRule.expectMessage("Brand with given ID does not exist");
        brandDto.getBrandById(id);
    }

    @Test
    @Rollback
    public void getAll() throws ApiException {
        List<BrandData> expectedBrandDataList = new LinkedList<>();


        for (BrandPojo brand : MockUtil.BRANDS) {
            brand.setId(null);
            brandService.addBrand(brand);
            expectedBrandDataList.add(convertToBrandData(brand));
        }

        List<BrandData> actualBrandDataList = brandDto.getAllBrand();
        actualBrandDataList.sort(Comparator.comparing(BrandData::getId));
        expectedBrandDataList.sort(Comparator.comparing(BrandData::getId));

        AssertUtil.assertEqualList(expectedBrandDataList, actualBrandDataList, AssertUtil::assertEqualBrandData);
    }

    @Test
    @Rollback
    public void updateBrandWithValidInputs() throws ApiException {
        BrandPojo brand = MockUtil.getMockBrand();
        brandService.addBrand(brand);

        BrandForm brandForm = new BrandForm("New Name", "New Category");
        BrandData actual = brandDto.updateBrand(brand.getId(), brandForm);
        BrandData expected = new BrandData(brand.getId());

        expected.setName("new name");
        expected.setCategory("new category");
        AssertUtil.assertEqualBrandData(expected, actual);
    }

}
