package com.increff.employee.dto;


import com.increff.employee.model.BrandForm;
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
import static com.increff.employee.util.Normalization.normalize;
import static com.increff.employee.util.ValidationUtil.isBlank;
import static com.increff.employee.util.ValidationUtil.isNegative;
//import static sun.java2d.loops.GraphicsPrimitive.convertTo;

@Component
public class ProductDto {


    @Autowired
    private ProductService productService;

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        if(isBlank(barcode)){
            throw new ApiException("barcode cannot be empty");
        }
        barcode = normalize(barcode);
        ProductPojo product = productService.getAndCheckProductByBarcode(barcode);
        BrandPojo brand = productService.getBrandByProduct(product);
        return convertToProductData(product, brand);
    }

    public void addProduct(ProductForm form) throws ApiException {
        validateFormData(form);
        normalizeFormData(form);
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
    public void update(Integer id, ProductForm form) throws ApiException  {
        validateFormData(form);
        normalizeFormData(form);
        BrandPojo brand = productService.checkBrandNameAndCategory( form.getBrandName(), form.getBrandCategory());
        ProductPojo product = productService.getProductById(id, form.getBarcode());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());
        productService.update(productPojo,  id, brand);


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
    private void validateFormData(ProductForm form) throws ApiException {
        if(isBlank(form.getName())){
            throw new ApiException("name cannot be empty");
        }
        if(isBlank(form.getBrandName())){
            throw new ApiException("brand cannot be empty");
        }
        if(isBlank(form.getBrandCategory())){
            throw new ApiException("category cannot be empty");
        }
        if(isNegative(form.getPrice())){
            throw new ApiException("enter a valid price");
        }
        if (isBlank(form.getBarcode())) {
            throw new ApiException("barcode cannot be empty");
        }
    }

    private void normalizeFormData(ProductForm form){
        form.setName(normalize(form.getName()));
        form.setBrandName(normalize(form.getBrandName()));
        form.setBrandCategory(normalize(form.getBrandCategory()));
        form.setBarcode(normalize(form.getBarcode()));
        form.setPrice(normalize(form.getPrice()));
    }

}
