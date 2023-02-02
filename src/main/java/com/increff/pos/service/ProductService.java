package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
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

    @Transactional(rollbackOn = ApiException.class)
    public void update(ProductPojo product) throws ApiException {
        normalizeProduct(product);
        ProductPojo exist = checkProduct(product.getId());
        exist.setName(product.getName());
        exist.setPrice(product.getPrice());
        exist.setBrandId(product.getBrandId());
    }

    private ProductPojo checkIfProductExist(ProductPojo p, String identity) throws ApiException {
        if (p == null) {
            throw new ApiException("Product with given " + identity + " does not exist");
        }
        return p;
    }

    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductByBarcode(barcode);
        /// FIXED: 29/01/23 below code can be combined with getProductById method
        return checkIfProductExist(p, "barcode");
    }
    public ProductPojo getProductById(Integer id) throws ApiException {
        ProductPojo p = dao.getProductById(id);
        /// FIXED: 29/01/23 below code can be combined with getProductById method
        return checkIfProductExist(p, "barcode");
    }

    public ProductPojo checkProduct(Integer productId) throws ApiException {
        ProductPojo p = dao.getProductById(productId);
        return checkIfProductExist(p, "id");
    }

    public ProductPojo checkProductByIdAndBarcode(Integer id, String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductById(id);
        if (p == null) {
            throw new ApiException("Product with given id does not exit, id: " + id);
        }
        if (p.getBarcode().equals(barcode)) {
            return p;
        }
        checkBarcode(barcode);
        return p;
    }

    public void validateSellingPrice(Float sellingPrice, Float price) throws ApiException {
        if (sellingPrice > price) {
            throw new ApiException("selling price should not be more than the MRP = "+price);
        }
    }

    private void checkBarcode(String barcode) throws ApiException {
        normalize(barcode);
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p != null) {
            throw new ApiException(("barcode already exist "));
        }

    }

    public List<ProductPojo> getProductsByIds(List<Integer> productIds) {
        List<ProductPojo> products = new ArrayList<>();
        for (Integer productId : productIds) {
            products.add(dao.getProductById(productId));
        }
        return products;
    }


    public List<ProductPojo> getProductsByBarcodes(List<String> b) {
        List<String> barcodes = new ArrayList<>();
        for (String barcode : b) {
            barcodes.add(normalize(barcode));
        }
        List<ProductPojo> products = new ArrayList<>();
        for (String barcode : barcodes) {
            products.add(dao.getProductByBarcode(barcode));
        }
        return products;
    }


}
