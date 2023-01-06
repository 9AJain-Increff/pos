package com.increff.employee.dto;


import com.increff.employee.model.ProductData;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.pojo.EmployeePojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.BrandService;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.ProductService;
import com.increff.employee.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

import static com.increff.employee.util.ConversionUtil.*;
//import static sun.java2d.loops.GraphicsPrimitive.convertTo;

@Component
public class ProductDto {


    @Autowired
    private ProductService service;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private BrandService brandService;
    public ProductData get(String barcode) throws ApiException {
        ProductPojo b = service.getCheck(barcode);
        return convertToProductData(b);
    }
    public void addingProduct(ProductForm form) throws ApiException {
        System.out.println("ankur jainnnnnnnnnnn");
        ProductPojo p = convertToProductPojo(form);
        InventoryPojo i = convertToInventoryPojo(form.getBarcode());
//        if(StringUtil.isEmpty(p.getName())) {
//            throw new ApiException("name cannot be empty");
//        }
//        if(StringUtil.isEmpty(p.getBarcode())) {
//            throw new ApiException("barcode cannot be empty");
//        }
//        if(StringUtil.isEmpty(p.getBrandName())) {
//            throw new ApiException("Brand Name cannot be empty");
//        }
        Boolean brandExist =brandService.checkBrandExists(p.getBrandName(),p.getBrandCategory());
        Boolean nameExist = service.nameExist(p.getName());
        if(brandExist && !nameExist)
        {

            service.add(p);
            inventoryService.add(i);
        }

    }

    public void deleting(String barcode) throws ApiException  {
        service.delete(barcode);
    }


    public void updating(String barcode, ProductForm form) throws ApiException  {


        ProductPojo p = convertToProductPojo(form);
        if(StringUtil.isEmpty(p.getName())) {
            throw new ApiException("name cannot be empty");
        }
        if(StringUtil.isEmpty(p.getBarcode())) {
            throw new ApiException("barcode cannot be empty");
        }
        if(StringUtil.isEmpty(p.getBrandName())) {
            throw new ApiException("Brand Name cannot be empty");
        }

        Boolean brandExist =brandService.checkBrandExists(p.getBrandName(),p.getBrandCategory());
        Boolean nameExist = service.nameExist(p.getName());
        if(brandExist && !nameExist)
        {
            service.update(barcode, p);
        }

    }






    public List<ProductData> gettingAllProduct() {
        List<ProductPojo> list = service.getAll();
        System.out.println(list);
        List<ProductData> list2 = new ArrayList<ProductData>();
        for (ProductPojo p : list) {
            list2.add(convertToProductData(p));
        }
        return list2;
    }
//
//    private static ProductData convert(ProductPojo p) {
//        ProductData d = new ProductData();
//        d.setName(p.getName());
//        d.setBrandName(p.getBrandName());
//        d.setBrandCategory(p.getBrandCategory());
//        d.setBarcode(p.getBarcode());
//        d.setPrice(p.getPrice());
//        d.setId(p.getId());
//        return d;
//    }
//
//    private static  ProductPojo convert(ProductForm f) {
//        ProductPojo p = new ProductPojo();
//        p.setName(f.getName());
//        p.setBrandCategory(f.getBrandCategory());
//        p.setPrice(f.getPrice());
//        p.setBrandName(f.getBarcode());
//        p.setBrandName(f.getBrandName());
//        p.setBarcode(f.getBarcode());
//        return p;
//    }

}
