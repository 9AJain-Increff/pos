package com.increff.pos.dto;


import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.increff.pos.util.ConversionUtil.*;
import static com.increff.pos.util.Normalization.normalize;
import static com.increff.pos.util.ValidationUtil.isBlank;
import static com.increff.pos.util.ValidationUtil.isNegative;
//import static sun.java2d.loops.GraphicsPrimitive.convertTo;

@Component
public class ProductDto {


    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private InventoryService inventoryService;


    // TODO: 29/01/23 remove
    private List<String> getBarcodes(List<ProductPojo> products){
        List<String> barcodes = products.stream()
                .map(ProductPojo::getBarcode)
                .collect(Collectors.toList());
        return barcodes;
    }

    // TODO: 29/01/23 instead of this call brandApi from calling place only
    private List<Integer> getBrandIdList(List<ProductPojo> products){
        List<Integer> barcodes = products.stream()
                .map(ProductPojo::getBrandId)
                .collect(Collectors.toList());
        return barcodes;
    }

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        if(isBlank(barcode)){
            throw new ApiException("barcode cannot be empty");
        }
        // TODO: 29/01/23 move normalise to service
        barcode = normalize(barcode);
        ProductPojo product = productService.getAndCheckProductByBarcode(barcode);
        BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
        return convertToProductData(product, brand);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addProduct(ProductForm form) throws ApiException {
        validateFormData(form);
        // TODO: 29/01/23 move to service
        normalizeFormData(form);
        BrandPojo brand =  brandService.checkBrandExistByNameAndCategory(form.getBrandName(), form.getBrandCategory());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());
        InventoryPojo inventoryPojo = new InventoryPojo();

        productService.addProduct(productPojo);
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(0);
        // TODO: 29/01/23 what is use of sending productPojo? inventoryPojo has already productId
        inventoryService.addInventory(inventoryPojo, productPojo);
    }

//    public void delete(String barcode) throws ApiException  {
//        service.delete(barcode);
//    }

//    TODO can i throw the error from here AND what if the barcode changes from postman
    public void update(Integer id, ProductForm form) throws ApiException  {
        validateFormData(form);
        // TODO: 29/01/23 move to service
        normalizeFormData(form);
        BrandPojo brand =  brandService.checkBrandExistByNameAndCategory(form.getBrandName(), form.getBrandCategory());
        // TODO: 29/01/23 remove
        ProductPojo product = productService.getProductById(id, form.getBarcode());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());
        // TODO: 29/01/23 why passing brand? productPojo has brandId already irght?
        productService.update(productPojo,  id, brand);


    }






    public List<ProductData> getAllProduct() throws ApiException {
        List<ProductPojo> products = productService.getAllProduct();
        List<Integer> brandIds = getBrandIdList(products);
        List<BrandPojo> brands =brandService.getBrandsByBrandId(brandIds);
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
