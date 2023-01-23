package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.InventoryDao;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.util.StringUtil;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;


    @Transactional(rollbackOn = ApiException.class)
    public void addInventory(InventoryPojo p, ProductPojo product) throws ApiException {
        InventoryPojo exist = inventoryDao.selectInventoryByProductId(product.getId());
        if(exist == null){
            inventoryDao.insert(p);
        }
        else {
            p.setQuantity(p.getQuantity()+exist.getQuantity());
            update(p);
        }
    }

//    @Transactional
////    public void delete(String barcode) {
////        dao.delete(barcode);
////    }



    public InventoryPojo getAndCheckInventoryByProductId(Integer productId) throws ApiException {
        InventoryPojo p = inventoryDao.selectInventoryByProductId(productId);
        if (p == null) {
            throw new ApiException("Inventory with given barcode does not exit, id: " + productId);
        }
        return p;
    }

    @Transactional
    public List<InventoryPojo> getAllInventory() {
        return inventoryDao.selectAll();


    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(InventoryPojo p) throws ApiException {

        InventoryPojo ex = inventoryDao.selectInventoryByProductId(p.getProductId());
        ex.setQuantity((p.getQuantity()));
    }


}
