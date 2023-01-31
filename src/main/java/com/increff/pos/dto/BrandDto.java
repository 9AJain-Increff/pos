package com.increff.pos.dto;


import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ConversionUtil.*;
import static com.increff.pos.util.ValidationUtil.validateBrandForm;

@Component
public class BrandDto {
    @Autowired
    private BrandService brandService;

    public BrandData getBrandById(int id) throws ApiException {
        BrandPojo brandPojo = brandService.getAndCheckBrandById(id);
        return convertToBrandData(brandPojo);
    }

    // FIXED: 29/01/23 why are you pojo to controller?
    public BrandData addBrand(BrandForm form) throws ApiException {
        validateBrandForm(form);
        BrandPojo brand = convertToBrandPojo(form);
        brandService.addBrand(brand);
        return convertToBrandData(brand);
    }

    public BrandData updateBrand(int id, BrandForm form) throws ApiException {
        validateBrandForm(form);
        BrandPojo brandPojo = convertToBrandPojo(form);
        brandPojo.setId(id);
        BrandPojo b = brandService.update(brandPojo);
        return convertToBrandData(b);
    }

    public List<BrandData> getAllBrand() {
        List<BrandPojo> brands = brandService.getAllBrand();
        List<BrandData> brandsData = new ArrayList<BrandData>();
        for (BrandPojo brandPojo : brands) {
            brandsData.add(convertToBrandData(brandPojo));
        }
        return brandsData;
    }


}
