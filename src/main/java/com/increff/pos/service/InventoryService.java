package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;


    @Transactional(rollbackOn = ApiException.class)
    public void addInventory(InventoryPojo p, ProductPojo product) throws ApiException {
        InventoryPojo exist = inventoryDao.selectInventoryByProductId(product.getId());
        // TODO: 29/01/23 use a sep method to check the duplicate
        if(exist == null){
            inventoryDao.insert(p);
        }
        else {
            p.setQuantity(p.getQuantity()+exist.getQuantity());
            update(p);
        }
    }

    // TODO: 29/01/23 remove
//    @Transactional
////    public void delete(String barcode) {
////        dao.delete(barcode);
////    }



    public InventoryPojo getAndCheckInventoryByProductId(Integer productId) throws ApiException {
        InventoryPojo p = inventoryDao.selectInventoryByProductId(productId);
        if (p == null) {
            throw new ApiException("Inventory with given barcode does not exist, id: " + productId);
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
