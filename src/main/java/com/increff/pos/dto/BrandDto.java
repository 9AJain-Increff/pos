package com.increff.pos.dto;


import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
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
    private BrandService service;

    public BrandData getBrandById(int id) throws ApiException {
         BrandPojo brandPojo = service.getAndCheckBrandById(id);
         return convertToBrandData(brandPojo);
    }

    // FIXED: 29/01/23 why are you pojo to controller?
    public BrandData addBrand(BrandForm form) throws ApiException {
        validateBrandForm(form);
        BrandPojo brand = convertToBrandPojo(form);
        return convertToBrandData(brand);
    }

    public BrandData updateBrand(int id, BrandForm form) throws ApiException  {
        validateBrandForm(form);
        BrandPojo brandPojo = convertToBrandPojo(form);
        BrandPojo b = service.update(id,brandPojo);
        return convertToBrandData(b);
    }

    public List<BrandData> getAllBrand() {
        List<BrandPojo> brands = service.getAllBrand();
        List<BrandData> brandsData = new ArrayList<BrandData>();
        for (BrandPojo brandPojo : brands) {
            brandsData.add(convertToBrandData(brandPojo));
        }
        return brandsData;
    }


}
