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

    public BrandPojo checkBrandByName(String name, String category) throws ApiException {
       BrandPojo b = dao.getBrand(name,category);
       if(b== null){
           throw new ApiException("Brand Doesn't exist");
       }
       return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addBrand(BrandPojo brandPojo) throws ApiException {
        BrandPojo brand = dao.getBrand(brandPojo.getName(),brandPojo.getCategory());
        if(brand == null)
        dao.insert(brandPojo);
        else{
            throw new ApiException("Brand with given name and category already exist" );
        }
    }

    @Transactional
    public void delete(int id) throws ApiException {
        BrandPojo brand = dao.getBrandById(id);
        if(brand == null)
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        else
            dao.delete(id);
    }


    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo getBrandById(int id) throws ApiException {
        BrandPojo brandPojo = dao.getBrandById(id);
        if (brandPojo == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return brandPojo;
    }

    @Transactional
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
            brands.add((getBrandById(product.getBrandId())));
        }
        return brands;
    }





}
