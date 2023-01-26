package com.increff.employee.service;

import com.increff.employee.dto.BrandDto;
import com.increff.employee.model.BrandForm;
import com.increff.employee.pojo.BrandPojo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.assertEquals;

public class BrandDtoTest extends AbstractUnitTest{
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
        BrandPojo brand = brandDto.addBrand(brandForm);
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

//    @Test
//    @Rollback
//    public void addBrandThrowsExceptionForBrandAlreadyExist() throws ApiException {
//        BrandForm brandForm = new BrandForm();
//        brandForm.setName(" ");
//        brandForm.setCategory("shoes");
//        exceptionRule.expect(ApiException.class);
//        exceptionRule.expectMessage("name cannot be empty");
//        brandDto.addBrand(brandForm);
//
//    }
//
//    @Test
//    @Rollback
//    public void getByValidId() throws ApiException {
//        BrandPojo brand = MockUtils.getMockBrand();
//        brandService.add(brand);
//
//        int id = brand.getId();
//        BrandData actual = brandApiDto.get(id);
//        BrandData expected = ConversionUtil.convertPojoToData(brand);
//
//        Assert.assertEquals(expected.getId(), actual.getId());
//        Assert.assertEquals(expected.getName(), actual.getName());
//        Assert.assertEquals(expected.getCategory(), actual.getCategory());
//    }

}
