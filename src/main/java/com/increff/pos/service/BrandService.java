package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.pos.util.Normalization.normalize;
import static com.increff.pos.util.Normalizationutil.normalizeBrand;


@Service
public class BrandService {

    @Autowired
    private BrandDao dao;


    private BrandPojo isDuplicate(BrandPojo b) throws ApiException {
        BrandPojo brand = dao.getBrandByNameAndCategory(b.getName(), b.getCategory());
        if (brand != null) {
            throw new ApiException("Brand with given name and category already exist");
        } else {
            return brand;
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo addBrand(BrandPojo brandPojo) throws ApiException {
        normalizeBrand(brandPojo);
        isDuplicate(brandPojo);
        dao.addBrand(brandPojo);
        return brandPojo;
    }


    public BrandPojo getAndCheckBrandById(int id) throws ApiException {
        BrandPojo brandPojo = dao.getBrandById(id);
        if (brandPojo == null) {
            throw new ApiException("Brand with given ID does not exist");
        }
        return brandPojo;
    }

    public List<BrandPojo> getAllBrand() {
        return dao.getAllBrand();
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo update(BrandPojo p) throws ApiException {
        normalizeBrand(p);
        // FIXED: 29/01/23 can use getAndCheckBrandById?
        BrandPojo exist = getAndCheckBrandById(p.getId());
        BrandPojo brand = getBrandByNameAndCategory(p.getName(), p.getCategory());
        if (brand != null && !brand.getId().equals(exist.getId())) {
            throw new ApiException("Brand with given name and category already exist");
        } else {
            exist.setName(p.getName());
            exist.setCategory(p.getCategory());
            return exist;
        }
    }


    public BrandPojo checkBrandExistByNameAndCategory(String brandName, String brandCategory) throws ApiException {
        // TODO: 29/01/23 why not pass BrandPojo instead of passing both as sep variables?  ....in product form we have brand and category ... in order to get brandpojo , still we have tto pass name and category
        normalize(brandName);
        normalize(brandCategory);
        BrandPojo brand = dao.getBrandByNameAndCategory(brandName, brandCategory);
        if (brand == null)
            throw new ApiException("Brand with given name and category does not exit ");
        return brand;
    }

    // FIXED: 29/01/23 remove throws
    public BrandPojo getBrandByNameAndCategory(String brandName, String brandCategory) {
        BrandPojo brand = dao.getBrandByNameAndCategory(brandName, brandCategory);
        return brand;
    }


    public List<BrandPojo> getBrandsByBrandId(List<Integer> braneIdList) throws ApiException {
        List<BrandPojo> brands = new ArrayList<>();
        for (int brandId : braneIdList) {
            brands.add((getAndCheckBrandById(brandId)));
        }
        return brands;
    }


}