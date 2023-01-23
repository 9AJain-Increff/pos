package com.increff.employee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.increff.employee.dao.ProductDao;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.util.StringUtil;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Autowired
    private BrandService brandService;

    @Transactional(rollbackOn = ApiException.class)
    public void addProduct(@NotNull ProductPojo product) throws ApiException {
        checkBarcode(product.getBarcode());
        dao.add(product);
    }


    public List<ProductPojo> getAllProduct() throws ApiException {
        return dao.getAllProduct();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(ProductPojo product,  Integer id, BrandPojo brand) throws ApiException {
        ProductPojo exist = getProductById(id, product.getBarcode());
        exist.setName(product.getName());
        exist.setPrice(product.getPrice());
        exist.setBrandId(brand.getId());
    }


    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, id: " + barcode);
        }
        return p;
    }

    public ProductPojo getProductById(Integer id, String barcode) throws ApiException {
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
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, barcode: " + barcode);
        }
        return p;
    }


    public ProductPojo checkBarcode(String barcode) throws ApiException {

        ProductPojo p = dao.getProductByBarcode(barcode);
        if(p != null){
            throw new ApiException(("barcode already exist "));
        }

        return p;
    }




    public Map<String, ProductPojo> getProductsBybarcodes(List<String> barcodes ) {
        Map<String, ProductPojo> mapping = new HashMap<>();
        for(String barcode: barcodes){
            mapping.put(barcode,dao.getProductByBarcode(barcode));
        }
        return mapping;
    }

    public List<ProductPojo> getProducts(List<String> barcodes ) {
        List<ProductPojo> products = new ArrayList<>();
        for(String barcode: barcodes){
            products.add(dao.getProductByBarcode(barcode));
        }
        return products;
    }



    protected static void normalize(ProductPojo p) {
        p.setName(StringUtil.toLowerCase(p.getName()));
    }



}
