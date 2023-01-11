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
        ProductPojo product = service.getProductByBarcode(barcode);
        BrandPojo brand = brandService.getBrandById(product.getBrandId());
        return convertToProductData(product, brand);
    }

    public void addProduct(ProductForm form) throws ApiException {
        BrandPojo brand = service.getBrandId(form.getBrandName(), form.getBrandCategory());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());
        InventoryPojo inventoryPojo = convertToInventoryPojo(productPojo.getBarcode());

        service.addProduct(productPojo, inventoryPojo, brand);


    }

    public void delete(String barcode) throws ApiException  {
        service.delete(barcode);
    }


    public void update(String barcode, ProductForm form) throws ApiException  {

        BrandPojo brand = service.getBrandId(form.getBrandName(), form.getBrandCategory());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());

        service.update(productPojo, brand, barcode);

    }






    public List<ProductData> getAllProduct() throws ApiException {
        List<ProductPojo> products = service.getAllProduct();
        List<BrandPojo> brands = brandService.getBrandsByProducts(products);
        List<ProductData> productsData = new ArrayList<ProductData>();

        for(int index = 0; index < products.size(); index++){
            productsData.add(convertToProductData(products.get(index),brands.get(index)));
        }
        return productsData;
    }


}
