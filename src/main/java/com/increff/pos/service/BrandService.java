package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.pos.util.Normalization.normalize;
import static com.increff.pos.util.Normalizationutil.normalizeBrand;


@Service
/**
 * Todo
 * missed following points which are mentioned in the doc
 *
 * 1. @Transactional has to be at class level with required rollback fields
 */
public class BrandService {

    @Autowired
    private BrandDao dao;


    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo addBrand(BrandPojo brandPojo) throws ApiException {
        normalizeBrand(brandPojo);
        BrandPojo brand = dao.getBrand(brandPojo.getName(),brandPojo.getCategory());
        // TODO: 29/01/23 create isDuplicateMethod and use it
        if(brand == null) {
            dao.addBrand(brandPojo);
            return brandPojo;
        }
        else{
            throw new ApiException("Brand with given name and category already exist" );
        }
    }



    public BrandPojo getAndCheckBrandById(int id) throws ApiException {
        BrandPojo brandPojo = dao.getBrandById(id);
        if (brandPojo == null) {
            throw new ApiException("Brand with given ID does not exist, id: " + id);
        }
        return brandPojo;
    }

    public List<BrandPojo> getAllBrand() {
        return dao.getAllBrand();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public BrandPojo update(int id, BrandPojo p) throws ApiException {
        normalizeBrand(p);
        // TODO: 29/01/23 can use getAndCheckBrandById?
        BrandPojo exist = dao.getBrandById(id);
        if(exist == null)
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        BrandPojo brand = getBrandByNameAndCategory(p.getName(), p.getCategory());
        if(brand != null && !brand.getId().equals(exist.getId())){
            throw new ApiException("Brand with given name and category already exist");
        }
        else {
            exist.setName(p.getName());
            exist.setCategory(p.getCategory());
            return exist;
        }
    }


    public BrandPojo checkBrandExistByNameAndCategory(String brandName, String brandCategory) throws ApiException {
        // TODO: 29/01/23 why not pass BrandPojo instead of passing both as sep variables?  ....in product form we have brand and category ... in order to get brandpojo , still we have tto pass name and category
        normalize(brandName);
        normalize(brandCategory);
        BrandPojo brand = dao.getBrand(brandName, brandCategory);
        if(brand == null)
            throw new ApiException("Brand with given name and category does not exit " );
        return brand;
    }

    // FIXED: 29/01/23 remove throws
    public BrandPojo getBrandByNameAndCategory(String brandName, String brandCategory)  {
        BrandPojo brand = dao.getBrand(brandName, brandCategory);
        return brand;
    }



    public List<BrandPojo> getBrandsByBrandId(List<Integer> braneIdList) throws ApiException {
        List<BrandPojo> brands = new ArrayList<>();
        for(int brandId : braneIdList){
            brands.add((getAndCheckBrandById(brandId)));
        }
        return brands;
    }


}
