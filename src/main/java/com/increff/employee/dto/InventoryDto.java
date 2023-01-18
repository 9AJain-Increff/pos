package com.increff.employee.dto;


import com.increff.employee.model.InventoryData;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.increff.employee.util.ConversionUtil.convertToInventoryData;
import static com.increff.employee.util.ConversionUtil.convertToInventoryPojo;
import static com.increff.employee.util.Normalization.normalize;
import static com.increff.employee.util.ValidationUtil.isBlank;
import static com.increff.employee.util.ValidationUtil.isNegative;

@Component
public class InventoryDto {
    @Autowired
    private InventoryService inventoryService;

    public InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        barcode = normalize(barcode);
        InventoryPojo b = inventoryService.getAndCheckInventoryByBarcode(barcode);
        return convertToInventoryData(b);
    }
    public void addInventory(InventoryForm form) throws ApiException {
        form.setBarcode(normalize(form.getBarcode()));
        validateFormData(form);
        InventoryPojo p = convertToInventoryPojo(form);
        inventoryService.addInventory(p);
    }

    public void updateInventory(InventoryForm form) throws ApiException  {
        form.setBarcode(normalize(form.getBarcode()));
        validateFormData(form);
        InventoryPojo p = convertToInventoryPojo(form);
        ProductPojo product = inventoryService.checkProductExists(p.getBarcode());
        inventoryService.update(p);
    }
    private List<String> getBarcodes(List<InventoryPojo> inventoryList){
        List<String> barcodes = inventoryList.stream()
                .map(InventoryPojo::getBarcode)
                .collect(Collectors.toList());
        return barcodes;
    }

    public List<InventoryData> getAllInventory() throws ApiException {
        List<InventoryPojo> inventoryList = inventoryService.getAllInventory();
        List<String> barcodes = getBarcodes(inventoryList);
        List<ProductPojo> productList = inventoryService.getProductsByBarcodes(barcodes);
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();


        System.out.println(productList.size());
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
