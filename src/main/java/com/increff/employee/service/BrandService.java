package com.increff.employee.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.BrandDao;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandService {

    @Autowired
    private BrandDao dao;


    @Transactional(rollbackOn = ApiException.class)
    public void addBrand(BrandPojo brandPojo) throws ApiException {
        BrandPojo brand = dao.getBrand(brandPojo.getName(),brandPojo.getCategory());
        if(brand == null)
        dao.insert(brandPojo);
        else{
            throw new ApiException("Brand with given name and category already exist" );
        }
    }



    public BrandPojo getAndCheckBrandById(int id) throws ApiException {
        BrandPojo brandPojo = dao.getBrandById(id);
        if (brandPojo == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return brandPojo;
    }

    public List<BrandPojo> getAllBrand() {
        return dao.getAllBrand();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, BrandPojo p) throws ApiException {
        BrandPojo exist = dao.getBrandById(id);
        if(exist == null)
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        else {
            exist.setName(p.getName());
            exist.setCategory(p.getCategory());
        }
    }


    public BrandPojo getBrandId(String brandName, String brandCategory) throws ApiException {

        BrandPojo brand = dao.getBrand(brandName, brandCategory);
        if(brand == null)
            throw new ApiException("Brand with given name and category does not exit " );
        return brand;
    }

    public List<BrandPojo> getBrandsByProducts(List<ProductPojo> products) throws ApiException {
        List<BrandPojo> brands = new ArrayList<>();
        for(ProductPojo product : products){
            brands.add((getAndCheckBrandById(product.getBrandId())));
        }
        return brands;
    }





}
