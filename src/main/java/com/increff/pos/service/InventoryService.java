package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    @Transactional(rollbackOn = ApiException.class)
    public void addInventory(InventoryPojo p) throws ApiException {
        InventoryPojo exist = inventoryDao.selectInventoryByProductId(p.getProductId());
        if (exist == null) {
            inventoryDao.insert(p);
        } else {
            if(p.getQuantity()<0){
                if(-1*p.getQuantity()>exist.getQuantity()){
                    throw new ApiException("Total available quantity is "+ exist.getQuantity());
                }
            }
            p.setQuantity(p.getQuantity() + exist.getQuantity());
            update(p);
        }
    }


    public InventoryPojo getAndCheckInventoryByProductId(Integer productId) throws ApiException {
        InventoryPojo p = inventoryDao.selectInventoryByProductId(productId);
        if (p == null) {
            throw new ApiException("Inventory with given product id does not exit, id: ");
        }
        return p;
    }

    @Transactional
    public List<InventoryPojo> getAllInventory() {
        return inventoryDao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryPojo p) throws ApiException {

        InventoryPojo ex = inventoryDao.selectInventoryByProductId(p.getProductId());
        if (ex == null) {
            throw new ApiException("Inventory Doesn't exist");
        }
        ex.setQuantity((p.getQuantity()));
    }

    public InventoryPojo validateInventory(Integer productId, Integer requiredQuantity) throws ApiException {
        InventoryPojo inventory = getAndCheckInventoryByProductId(productId);
        if(requiredQuantity>inventory.getQuantity()){
            throw new ApiException("Only "+inventory.getQuantity()+" available in inventory");
        }
        return  inventory;
    }

}
