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
    @Autowired
    private ProductService productService;


    @Transactional(rollbackOn = ApiException.class)
    public void addInventory(InventoryPojo p) throws ApiException {
        ProductPojo product = productService.getAndCheckProductByBarcode(p.getBarcode());
        InventoryPojo exist = inventoryDao.select(p.getBarcode());
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


    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo getAndCheckInventoryByBarcode(String barcode) throws ApiException {
        InventoryPojo p = inventoryDao.select(barcode);
        if (p == null) {
            throw new ApiException("Inventory with given barcode does not exit, id: " + barcode);
        }
        return p;
    }

    @Transactional
    public List<InventoryPojo> getAllInventory() {
        return inventoryDao.selectAll();


    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(InventoryPojo p) throws ApiException {
        normalize(p);
        InventoryPojo ex = inventoryDao.select(p.getBarcode());
        ex.setQuantity((p.getQuantity()));
        ex.setBarcode(p.getBarcode());
    }

    public List<ProductPojo> getProductsByBarcodes(List<String> barcodes) throws ApiException {
       List<ProductPojo> productList =  productService.getProducts(barcodes);
       return productList;
    }

    public ProductPojo checkProductExists(String barcode) throws ApiException {
        return productService.getProductByBarcode(barcode);
    }

    protected static void normalize(InventoryPojo p) {
        p.setBarcode(StringUtil.toLowerCase(p.getBarcode()));
    }
}
