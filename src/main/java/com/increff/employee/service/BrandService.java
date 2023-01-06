package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.BrandDao;
import com.increff.employee.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.dao.EmployeeDao;
import com.increff.employee.pojo.EmployeePojo;
import com.increff.employee.util.StringUtil;

@Service
public class BrandService {

    @Autowired
    private BrandDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandPojo p) throws ApiException {
        normalize(p);
        if(StringUtil.isEmpty(p.getName())) {
            throw new ApiException("name cannot be empty");
        }
        if(dao.checkBrandExists(p.getName(),p.getCategory())) {
            dao.insert(p);
        }
        else{
            throw new ApiException("brand name + category already exists");
        }

    }

    @Transactional
    public void delete(int id) {
        dao.delete(id);
    }


    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo get(int id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return p;
    }

    @Transactional
    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, BrandPojo p) throws ApiException {
        normalize(p);
        BrandPojo ex = dao.select(id);
        Boolean exist = checkBrandExists(p.getName(),p.getCategory());
        if(!exist){
            System.out.println(p.getName());
            System.out.println(p.getCategory());
            ex.setName(p.getName());
            ex.setCategory(p.getCategory());
        }
        else{
            throw new ApiException("brand name + category already exists");
        }

//        dao.update(ex);
    }

    @Transactional
    public Boolean getCheck(int id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return true;
    }

    public Boolean checkBrandExists(String name,String category) throws ApiException {
        BrandPojo p = dao.select(name, category);
        if (p == null) {
            throw new ApiException(("Brand name + Category not exist"));

        }
        return  true;

    }





    protected static void normalize(BrandPojo p) {
        p.setName(StringUtil.toLowerCase(p.getName()));
    }
}
