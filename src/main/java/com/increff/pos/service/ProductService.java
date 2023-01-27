package com.increff.pos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.increff.pos.util.Normalization.normalize;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Autowired
    private BrandService brandService;

    @Transactional(rollbackOn = ApiException.class)
    public void addProduct(@NotNull ProductPojo product) throws ApiException {
        normalization(product);
        checkBarcode(product.getBarcode());
        dao.add(product);
    }


    public List<ProductPojo> getAllProduct() throws ApiException {
        return dao.getAllProduct();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(ProductPojo product,  Integer id, BrandPojo brand) throws ApiException {
        normalization(product);
        ProductPojo exist = getProductById(id, product.getBarcode());
        exist.setName(product.getName());
        exist.setPrice(product.getPrice());
        exist.setBrandId(brand.getId());
    }


    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, id: " + barcode);
        }
        return p;
    }
    public ProductPojo getProductById(Integer productId) throws ApiException {
        ProductPojo p = dao.getProductById(productId);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, id: " + productId);
        }
        return p;
    }

    public ProductPojo getProductById(Integer id, String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductById(id);
        if (p == null) {
            throw new ApiException("Product with given id does not exit, id: " + id);
        }
        if(!barcode.equals(p.getBarcode())){
            throw new ApiException("product's barcode is "+p.getBarcode()+" , can't be changed");
        }
        return p;
    }

    public ProductPojo  getAndCheckProductByBarcode(String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, barcode: " + barcode);
        }
        return p;
    }


    public ProductPojo checkBarcode(String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductByBarcode(barcode);
        if(p != null){
            throw new ApiException(("barcode already exist "));
        }

        return p;
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



    private  void normalization(ProductPojo p) {
        normalize(p.getBarcode());
        normalize(p.getName());
        normalize(p.getPrice());
    }



}
