package com.increff.employee.dto;


import com.increff.employee.model.ProductData;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.BrandService;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.ProductService;
import com.increff.employee.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public ProductData getProductByBarcode(String barcode) throws ApiException {
        ProductPojo b = service.getProductByBarcode(barcode);
        return convertToProductData(b);
    }
    @Transactional(rollbackOn = ApiException.class)
    public void addProduct(ProductForm form) throws ApiException {

        ProductPojo productPojo = convertToProductPojo(form);
        InventoryPojo inventoryPojo = convertToInventoryPojo(productPojo.getBarcode());
        BrandPojo brand = brandService.getBrandByName(productPojo.getBrandName(),productPojo.getBrandCategory());

        if(brand == null){
            throw new ApiException("Brand Doesn't exist");
        }
        else{
            ProductPojo existingProduct = service.getProductByName(productPojo.getName());

            if( existingProduct == null || !Objects.equals(existingProduct.getName(), productPojo.getName())){
                service.addProduct(productPojo);
                inventoryService.add(inventoryPojo);
            }
            else{
                throw new ApiException("Product already exist");
            }
        }


    }

    public void delete(String barcode) throws ApiException  {
        service.delete(barcode);
    }


    public void update(String barcode, ProductForm form) throws ApiException  {


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

        BrandPojo brand = brandService.getBrandByName(p.getBrandName(),p.getBrandCategory());
        if(brand == null){
            throw new ApiException("Brand Doesn't exist");
        }
        else{
            service.update(barcode, p);
        }

    }






    public List<ProductData> getAllProduct() {
        List<ProductPojo> products = service.getAllProduct();
        List<ProductData> productPojos = new ArrayList<ProductData>();
        for (ProductPojo p : products) {
            productPojos.add(convertToProductData(p));
        }
        return productPojos;
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
