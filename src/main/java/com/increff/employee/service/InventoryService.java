package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.InventoryDao;
import com.increff.employee.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.dao.EmployeeDao;
import com.increff.employee.pojo.EmployeePojo;
import com.increff.employee.util.StringUtil;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo p) throws ApiException {
        normalize(p);

        dao.insert(p);

    }

    @Transactional
    public void delete(String barcode) {
        dao.delete(barcode);
    }


    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo get(String barcode) throws ApiException {
        InventoryPojo p = dao.select(barcode);
        if (p == null) {
            throw new ApiException("Inventory with given ID does not exit, id: " + barcode);
        }
        return p;
    }

    @Transactional
    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(String barcode, InventoryPojo p) throws ApiException {
        normalize(p);
        InventoryPojo ex = dao.select(barcode);

            ex.setQuantity((p.getQuantity()));
            ex.setBarcode(p.getBarcode());


//        dao.update(ex);
    }

    @Transactional
    public Boolean getCheck(String barcode) throws ApiException {
        InventoryPojo p = dao.select(barcode);
        if (p == null) {
            return true;
        }
        else{
            throw new ApiException("Inventory with given barcode already exit, barcode: " + barcode);
        }

    }

    public Boolean checkInventoryExists(String name,String category) throws ApiException {
        InventoryPojo p = dao.select(name, category);
        if (p == null) {
            throw new ApiException(("Inventory name + Category not exist"));

        }
        return  true;

    }





    protected static void normalize(InventoryPojo p) {
        p.setBarcode(StringUtil.toLowerCase(p.getBarcode()));
    }
}
