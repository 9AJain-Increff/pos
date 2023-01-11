package com.increff.employee.dto;


import com.increff.employee.model.InventoryData;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.increff.employee.util.ConversionUtil.convertToInventoryData;
import static com.increff.employee.util.ConversionUtil.convertToInventoryPojo;

@Component
public class InventoryDto {



    @Autowired
    private InventoryService service;

    @Autowired
    private ProductService productService;


    public InventoryData get(String barcode) throws ApiException {
        InventoryPojo b = service.get(barcode);
        return convertToInventoryData(b);
    }
    public void addInventory(InventoryForm form) throws ApiException {
        InventoryPojo p = convertToInventoryPojo(form);

        service.add(p);



    }

    public void deleting(String barcode) throws ApiException  {
        service.delete(barcode);
    }


    public void updating( InventoryForm form) throws ApiException  {

        InventoryPojo p = convertToInventoryPojo(form);
//        Boolean inventoryExist = service.getCheck(p.getBarcode());
        Boolean productExist = productService.checkProductExists(p.getBarcode());
        if( productExist){
            service.update(p);
        }
    }

    public List<InventoryData> getAllInventory() throws ApiException {
        List<InventoryPojo> inventoryList = service.getAllInventory();

        List<ProductPojo> productList = service.getProductByInventory(inventoryList);
//        System.out.println(list);
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();


        System.out.println(productList.size());
        for (int i = 0; i < inventoryList.size(); i++) {
            inventoryDataList.add(convertToInventoryData(inventoryList.get(i), productList.get(i)));
        }

        return inventoryDataList;
    }

//    private static InventoryData convert(InventoryPojo p) {
//        InventoryData d = new InventoryData();
//        d.setBarcode(p.getBarcode());
//        d.setQuantity(p.getQuantity());
//        return d;
//    }
//
//    private static InventoryData convert(InventoryPojo i,ProductPojo p) {
//        System.out.println("ankur jain");
//
//        InventoryData d = new InventoryData();
//        System.out.println(p.getBarcode());
//
//        d.setBarcode(p.getBarcode());
//        d.setQuantity(i.getQuantity());
//        d.setProductName(p.getName());
//        d.setId(p.getId());
//        return d;
//    }
//
//    private static  InventoryPojo convert(InventoryForm f) {
//        InventoryPojo p = new InventoryPojo();
//        p.setBarcode(f.getBarcode());
//        p.setQuantity(f.getQuantity());
//        return p;
//    }

}
