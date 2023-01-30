package com.increff.pos.dto;


import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ConversionUtil.convertToInventoryData;
import static com.increff.pos.util.ConversionUtil.convertToInventoryPojo;
import static com.increff.pos.util.Normalization.normalize;
import static com.increff.pos.util.ValidationUtil.*;

@Component
// FIXED: 29/01/23 every add/edit methd should return something like data or id
public class InventoryDto {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;

    public BrandData.InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        // TODO: 29/01/23 normalisation should happen inside service

        ProductPojo product = productService.getProductByBarcode(barcode);
        InventoryPojo b = inventoryService.getAndCheckInventoryByProductId(product.getId());
        return convertToInventoryData(b, product);
    }

    public BrandData.InventoryData addInventory(InventoryForm form) throws ApiException {
        // TODO: 29/01/23 normalisation should happen inside service if its already happening there then there is no need of this
        validateInventoryForm(form);
        ProductPojo product = productService.getProductByBarcode(form.getBarcode());
        InventoryPojo p = convertToInventoryPojo(form, product.getId());
        inventoryService.addInventory(p);
        return convertToInventoryData(p, product);
    }

    public BrandData.InventoryData updateInventory(InventoryForm form) throws ApiException  {
        // TODO: 29/01/23 normalisation should happen inside service if its already happening there then there is no need of this
        form.setBarcode(normalize(form.getBarcode()));
        validateInventoryForm(form);
        ProductPojo product = productService.getProductByBarcode(form.getBarcode());
        InventoryPojo p = convertToInventoryPojo(form, product.getId());
        inventoryService.update(p);
        return convertToInventoryData(p,product);
    }

    public List<String> getBarcodes(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<String> barcodes = new ArrayList<>();
        // TODO: 29/01/23 orderItem?
        for(InventoryPojo inventory : inventoryPojoList){
            ProductPojo product = productService.getProductById(inventory.getProductId());
            barcodes.add(product.getBarcode());
        }
        return barcodes;
    }

    // TODO: 29/01/23 iterate over inventoryList only once and poplate dont depend on indices 
    public List<BrandData.InventoryData> getAllInventory() throws ApiException {
        List<InventoryPojo> inventoryList = inventoryService.getAllInventory();
        List<String> barcodes = getBarcodes(inventoryList);
        List<ProductPojo> productList = productService.getProducts(barcodes);
        List<BrandData.InventoryData> inventoryDataList = new ArrayList<BrandData.InventoryData>();
        for (int i = 0; i < inventoryList.size(); i++) {
            inventoryDataList.add(convertToInventoryData(inventoryList.get(i), productList.get(i)));
        }

        return inventoryDataList;
    }


}
