package com.increff.pos.dto;


import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.exception.ApiException;
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
import static com.increff.pos.util.ValidationUtil.*;
//import static sun.java2d.loops.GraphicsPrimitive.convertTo;

@Component
public class ProductDto {
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private InventoryService inventoryService;


    // TODO: 29/01/23 instead of this call brandApi from calling place only
    private List<Integer> getBrandIdList(List<ProductPojo> products) {
        List<Integer> brandIds = products.stream()
                .map(ProductPojo::getBrandId)
                .collect(Collectors.toList());
        return brandIds;
    }

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        if (isBlank(barcode)) {
            throw new ApiException("barcode cannot be empty");
        }
        ProductPojo product = productService.getProductByBarcode(barcode);
        BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
        return convertToProductData(product, brand);
    }

    public ProductData getProductById(Integer id) throws ApiException {
        ProductPojo product = productService.getProductById(id);
        BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
        return convertToProductData(product, brand);
    }


    @Transactional(rollbackOn = ApiException.class)
    public ProductData addProduct(ProductForm form) throws ApiException {
        validateProductForm(form);

        BrandPojo brand = brandService.checkBrandExistByNameAndCategory(form.getBrandName(), form.getBrandCategory());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());
        InventoryPojo inventoryPojo = new InventoryPojo();

        ProductPojo product = productService.addProduct(productPojo);
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(0);
        inventoryService.addInventory(inventoryPojo);
        return convertToProductData(product, brand);
        // FIXED: 29/01/23 what is use of sending productPojo? inventoryPojo has already productId
    }


    //    FIXED can i throw the error from here AND what if the barcode changes from postman
    public ProductData update(Integer id, ProductForm form) throws ApiException {
        validateProductForm(form);
        BrandPojo brand = brandService.checkBrandExistByNameAndCategory(form.getBrandName(), form.getBrandCategory());
        productService.checkProductByIdAndBarcode(id, form.getBarcode());
        ProductPojo productPojo = convertToProductPojo(form, brand.getId());
        productPojo.setId(id);
        // FIXED: 29/01/23 why passing brand? productPojo has brandId already irght?
        productService.update(productPojo);
        return convertToProductData(productPojo, brand);

    }

    public List<ProductData> getAllProduct() throws ApiException {
        List<ProductPojo> products = productService.getAllProduct();
        List<Integer> brandIds = getBrandIdList(products);
        List<BrandPojo> brands = brandService.getBrandsByBrandId(brandIds);
        List<ProductData> productsData = new ArrayList<ProductData>();

        for (int index = 0; index < products.size(); index++) {
            productsData.add(convertToProductData(products.get(index), brands.get(index)));
        }
        return productsData;
    }


}
