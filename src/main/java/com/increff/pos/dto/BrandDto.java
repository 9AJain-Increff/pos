package com.increff.pos.dto;


import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ConversionUtil.*;
import static com.increff.pos.util.ValidationUtil.isBlank;

@Component
public class BrandDto {
    @Autowired
    private BrandService service;

    public BrandData getBrandById(int id) throws ApiException {
         BrandPojo brandPojo = service.getAndCheckBrandById(id);
         return convertToBrandData(brandPojo);
    }

    public BrandPojo addBrand(BrandForm form) throws ApiException {
        validateFormData(form);
        BrandPojo p = convertToBrandPojo(form);
        return service.addBrand(p);
    }

    public BrandData updateBrand(int id, BrandForm form) throws ApiException  {
        validateFormData(form);
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

    private void validateFormData(BrandForm form) throws ApiException {
        if(isBlank(form.getName())){
            throw new ApiException("name cannot be empty");
        }
        if(isBlank(form.getCategory())){
            throw new ApiException("category cannot be empty");
        }
    }
}
