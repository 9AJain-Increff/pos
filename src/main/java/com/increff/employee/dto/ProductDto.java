package com.increff.employee.dto;


import com.increff.employee.model.ProductData;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.increff.employee.util.ConversionUtil.*;
//import static sun.java2d.loops.GraphicsPrimitive.convertTo;

@Component
public class ProductDto {


    @Autowired
    private ProductService productService;

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        ProductPojo product = productService.getAndCheckProductByBarcode(barcode);
        BrandPojo brand = productService.getBrandByProduct(product);
        return convertToProductData(product, brand);
    }

    public void addProduct(ProductForm form) throws ApiException {
        BrandPojo brand = productService.getAndCheckBrandId(form.getBrandName(), form.getBrandCategory());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setBarcode(productPojo.getBarcode());
        inventoryPojo.setQuantity(0);
        productService.addProduct(productPojo, inventoryPojo, brand);
    }

//    public void delete(String barcode) throws ApiException  {
//        service.delete(barcode);
//    }

//    TODO can i throw the error from here AND what if the barcode changes from postman
    public void update(String barcode, ProductForm form) throws ApiException  {
        ProductPojo product = productService.getAndCheckProductByBarcode(barcode);
        BrandPojo brand = productService.getBrandByProduct(product);
        if(brand.getName().equals(form.getBrandName()) && brand.getCategory().equals(form.getBrandCategory())){
            ProductPojo productPojo = convertToProductPojo(form, brand.getId());
            productService.update(productPojo, brand, barcode);
        }
        else{
            throw new ApiException("brand name and category can't be changed");
        }


    }






    public List<ProductData> getAllProduct() throws ApiException {
        List<ProductPojo> products = productService.getAllProduct();
        List<BrandPojo> brands = productService.getBrandsByProducts(products);
        List<ProductData> productsData = new ArrayList<ProductData>();

        for(int index = 0; index < products.size(); index++){
            productsData.add(convertToProductData(products.get(index),brands.get(index)));
        }
        return productsData;
    }


}
