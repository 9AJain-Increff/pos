package com.increff.employee.service;

import java.util.ArrayList;
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
    private InventoryDao dao;
    @Autowired
    private ProductService productService;


    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo p) throws ApiException {
        ProductPojo product = productService.getProductByBarcode(p.getBarcode());
        InventoryPojo exist = dao.select(p.getBarcode());
        if(exist == null){
            dao.insert(p);
        }
        else {
            update(p);
        }
    }

    @Transactional
    public void delete(String barcode) {
        dao.delete(barcode);
    }

    public List<InventoryPojo> getInventoryPojo(List<String> barcode) throws ApiException {
        List<InventoryPojo> inventoryPojoList = new ArrayList<>();
        for (String temp : barcode) {
            InventoryPojo inventoryPojo = get(temp);
            inventoryPojoList.add(inventoryPojo);
//            if (temp.getQuantity() > inventoryPojo.getQuantity()) {
//                throw new ApiException("Maximum available quantity for the barcode" + temp.getBarcode() + " is " + inventoryPojo.getQuantity());
//            }
//            else{
//                inventoryPojo.setQuantity(inventoryPojo.getQuantity()-temp.getQuantity());
//                update(temp.getBarcode(),inventoryPojo);
//            }
        }
        return inventoryPojoList;
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
    public List<InventoryPojo> getAllInventory() {
        return dao.selectAll();


    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(InventoryPojo p) throws ApiException {
        normalize(p);
        InventoryPojo ex = dao.select(p.getBarcode());
        ex.setQuantity((p.getQuantity()+ex.getQuantity()));
        ex.setBarcode(p.getBarcode());


//        dao.update(ex);
    }

    public InventoryPojo getInventoryByBarcode(String barcode) throws ApiException {
        return dao.select(barcode);
    }

    public Boolean checkInventoryExists(String name,String category) throws ApiException {
        InventoryPojo p = dao.select(name, category);
        if (p == null) {
            throw new ApiException(("Inventory name + Category not exist"));

        }
        return  true;

    }

    public List<ProductPojo>   getProductByInventory(List<InventoryPojo> inventoryList) throws ApiException {
       List<ProductPojo> productList =  productService.getProductByBarcode(inventoryList);
       return productList;
    }



    protected static void normalize(InventoryPojo p) {
        p.setBarcode(StringUtil.toLowerCase(p.getBarcode()));
    }
}
