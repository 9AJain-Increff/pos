package com.increff.pos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.pos.util.Normalization.normalize;
import static com.increff.pos.util.Normalizationutil.normalizeProduct;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;


    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo addProduct(@NotNull ProductPojo product) throws ApiException {
        normalizeProduct(product);
        checkBarcode(product.getBarcode());
        return dao.add(product);
    }


    // FIXED: 29/01/23 remove ApiException
    public List<ProductPojo> getAllProduct() {
        return dao.getAllProduct();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(ProductPojo product,  Integer id) throws ApiException {
        normalizeProduct(product);
        ProductPojo exist = getProductById(id, product.getBarcode());
        exist.setName(product.getName());
        exist.setPrice(product.getPrice());
        exist.setBrandId(product.getBrandId());
    }

    private ProductPojo checkIfProductExist(ProductPojo p, String identity) throws ApiException {
        if (p == null) {
            throw new ApiException("Product with given "+ identity +" does not exist" );
        }
        return p;
    }

    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductByBarcode(barcode);
        /// FIXED: 29/01/23 below code can be combined with getProductById method
        return checkIfProductExist(p,"barcode");
    }
    public ProductPojo getProductById(Integer productId) throws ApiException {
        ProductPojo p = dao.getProductById(productId);
        return checkIfProductExist(p,"id");
    }

    public ProductPojo getProductById(Integer id, String barcode) throws ApiException {
        normalize(barcode);
        // FIXED: 29/01/23 cant we use getProductById?
        ProductPojo p = dao.getProductById(id);
        if (p == null) {
            throw new ApiException("Product with given id does not exit, id: " + id);
        }
        if(p.getBarcode() == barcode){
            return p;
        }
        // FIXED: 29/01/23 barcode can be changed but not to an existing one.....(?)
        checkBarcode(barcode);
        return p;
    }
    public void validateSellingPrice(Float sellingPrice, Float price) throws ApiException {
        if(sellingPrice > price){
            throw new ApiException("selling price should not be more than the MRP!");
        }
    }



    // FIXED: 29/01/23 public is needed?
    private void checkBarcode(String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductByBarcode(barcode);
        if(p != null){
            throw new ApiException(("barcode already exist "));
        }

    }




    public Map<Integer, ProductPojo> getProductsByProductIds(List<Integer> productIds ) {
        Map<Integer, ProductPojo> mapping = new HashMap<>();
        for(Integer productId: productIds){
            mapping.put(productId,dao.getProductById(productId));
        }
        return mapping;
    }


    public List<ProductPojo> getProducts(List<String> b) {
        List<String> barcodes = new ArrayList<>();
        for (String barcode: b){
            barcodes.add(normalize(barcode));
        }
        List<ProductPojo> products = new ArrayList<>();
        for(String barcode: barcodes){
            products.add(dao.getProductByBarcode(barcode));
        }
        return products;
    }




}
