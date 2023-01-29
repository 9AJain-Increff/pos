package com.increff.pos.dto;


import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
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
import static com.increff.pos.util.ValidationUtil.isBlank;
import static com.increff.pos.util.ValidationUtil.isNegative;

@Component
public class InventoryDto {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;

    public InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        barcode = normalize(barcode);
        ProductPojo product = productService.getProductByBarcode(barcode);
        InventoryPojo b = inventoryService.getAndCheckInventoryByProductId(product.getId());
        return convertToInventoryData(b, product);
    }
    public void addInventory(InventoryForm form) throws ApiException {
        form.setBarcode(normalize(form.getBarcode()));
        validateFormData(form);
        ProductPojo product = productService.getAndCheckProductByBarcode(form.getBarcode());
        InventoryPojo p = convertToInventoryPojo(form, product.getId());
        inventoryService.addInventory(p);
    }

    public void updateInventory(InventoryForm form) throws ApiException  {
        form.setBarcode(normalize(form.getBarcode()));
        validateFormData(form);
        ProductPojo product = productService.getProductByBarcode(form.getBarcode());
        InventoryPojo p = convertToInventoryPojo(form, product.getId());
        inventoryService.update(p);
    }

    public List<String> getBarcodes(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<String> barcodes = new ArrayList<>();
        for(InventoryPojo orderItem : inventoryPojoList){
            ProductPojo product = productService.getProductById(orderItem.getProductId());
            barcodes.add(product.getBarcode());
        }
        return barcodes;
    }
    public List<InventoryData> getAllInventory() throws ApiException {
        List<InventoryPojo> inventoryList = inventoryService.getAllInventory();
        List<String> barcodes = getBarcodes(inventoryList);
        List<ProductPojo> productList = productService.getProducts(barcodes);
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
        for (int i = 0; i < inventoryList.size(); i++) {
            inventoryDataList.add(convertToInventoryData(inventoryList.get(i), productList.get(i)));
        }

        return inventoryDataList;
    }

    private void validateFormData(InventoryForm form) throws ApiException {
        if(isBlank(form.getBarcode())){
            throw new ApiException("barcode cannot be empty");
        }

        if(isNegative(form.getQuantity())){
            throw new ApiException("quantity cannot be negative");
        }

    }
}
