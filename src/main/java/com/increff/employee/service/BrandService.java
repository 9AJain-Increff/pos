package com.increff.employee.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.BrandDao;
import com.increff.employee.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.employee.util.Normalization.normalize;

@Service
public class BrandService {

    @Autowired
    private BrandDao dao;


    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo addBrand(BrandPojo brandPojo) throws ApiException {
        normalization(brandPojo);
        BrandPojo brand = dao.getBrand(brandPojo.getName(),brandPojo.getCategory());
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
            throw new ApiException("Brand witFh given ID does not exit, id: " + id);
        }
        return brandPojo;
    }

    public List<BrandPojo> getAllBrand() {
        return dao.getAllBrand();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, BrandPojo p) throws ApiException {
        normalization(p);
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
        }
    }


    public BrandPojo checkBrandExist(String brandName, String brandCategory) throws ApiException {
        normalize(brandName);
        normalize(brandCategory);
        BrandPojo brand = dao.getBrand(brandName, brandCategory);
        if(brand == null)
            throw new ApiException("Brand with given name and category does not exit " );
        return brand;
    }
    public BrandPojo getBrandByNameAndCategory(String brandName, String brandCategory) throws ApiException {
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

    private void normalization(BrandPojo brand){
        normalize(brand.getName());
        normalize(brand.getCategory());
    }


}
