package com.increff.pos.util;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Component
public class Mock {

    public static BrandPojo getMockBrand() {
        BrandPojo mock = new BrandPojo();
        mock.setName("Mock Brand");
        mock.setCategory("Mock Category");
        return mock;
    }

    public static final List<BrandPojo> BRANDS = Arrays.asList(
            new BrandPojo(null, "phone", "apple"),
            new BrandPojo(null, "phone", "samsung"),
            new BrandPojo(null, "laptop", "lenovo"),
            new BrandPojo(null, "laptop", "apple"),
            new BrandPojo(null, "shoe", "nike")
    );



    public static List<BrandPojo> setUpBrands(BrandService brandService) {
        /*
         Total 5 mock brands are there:
         2 with category 'laptop', 2 with category 'phone' and 1 with 'shoe'
         */
        BRANDS.forEach(brand -> {
            try {
                brand.setId(null);
                brandService.addBrand(brand); // After add ID will be set
            } catch (ApiException ignored) {
            }
        });

        return BRANDS;
    }
}
