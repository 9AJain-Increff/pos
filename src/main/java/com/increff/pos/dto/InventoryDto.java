package com.increff.pos.dto;


import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.exception.ApiException;
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
public class InventoryDto {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;

    public InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        ProductPojo product = productService.getProductByBarcode(barcode);
        InventoryPojo b = inventoryService.getAndCheckInventoryByProductId(product.getId());
        return convertToInventoryData(b, product);
    }

    public InventoryData getInventoryByProductId(Integer id) throws ApiException {
        ProductPojo product = productService.getProductById(id);
        InventoryPojo b = inventoryService.getAndCheckInventoryByProductId(product.getId());
        return convertToInventoryData(b, product);
    }

    public InventoryData addInventory(InventoryForm form) throws ApiException {
        validateInventoryForm(form);
        ProductPojo product = productService.getProductByBarcode(form.getBarcode());
        InventoryPojo p = convertToInventoryPojo(form, product.getId());
        inventoryService.addInventory(p);
        return convertToInventoryData(p, product);
    }

    public List<String> getBarcodes(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<String> barcodes = new ArrayList<>();
        for (InventoryPojo inventory : inventoryPojoList) {
            ProductPojo product = productService.checkProduct(inventory.getProductId());
            barcodes.add(product.getBarcode());
        }
        return barcodes;
    }

    public List<InventoryData> getAllInventory() throws ApiException {
        List<InventoryPojo> inventoryList = inventoryService.getAllInventory();
        List<String> barcodes = getBarcodes(inventoryList);
        List<ProductPojo> productList = productService.getProductsByBarcodes(barcodes);
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
        for (int i = 0; i < inventoryList.size(); i++) {
            inventoryDataList.add(convertToInventoryData(inventoryList.get(i), productList.get(i)));
        }

        return inventoryDataList;
    }


}
