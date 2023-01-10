package com.increff.employee.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.ProductDao;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.util.StringUtil;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void addProduct(ProductPojo p) throws ApiException {
        normalize(p);

        System.out.println("ankur jainnnnnnnnnnn");
        ProductPojo check = dao.checkProductExists(p.getBarcode());
        if(check == null){
            dao.insert(p);
        }
        else{
            throw new ApiException(" barcode already exists");
        }

    }

    @Transactional
    public void delete(String barcode) {
        dao.delete(barcode);
    }


//    @Transactional(rollbackOn = ApiException.class)
//    public ProductPojo get(int id) throws ApiException {
//        return getCheck(id);
//    }

    @Transactional
    public List<ProductPojo> getAllProduct() {
        return dao.getAllProduct();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(String barcode, ProductPojo p) throws ApiException {
        normalize(p);
        ProductPojo ex = getProductByBarcode(barcode);
        ex.setName(p.getName());
        ex.setPrice(p.getPrice());
    }

    public List<ProductPojo> getProductPojo(List<String> barcode) throws ApiException {
        List<ProductPojo> productPojoList = new ArrayList<>();
        for (String orderItemBarcode : barcode) {
            ProductPojo productPojo = getByBarcode(orderItemBarcode);
            productPojoList.add(productPojo);
        }
        return productPojoList;
    }


    public List<ProductPojo> getProductByBarcode (List<InventoryPojo> list ) {
        System.out.println("anknanana");
        List<ProductPojo> list2 = new ArrayList<ProductPojo>();
        list.forEach((temp) -> {
            System.out.println("ppppppppppppp");
            System.out.println(temp.getBarcode());
                    ProductPojo p =dao.getProductByBarcode(temp.getBarcode());
                    list2.add(p);
                });

        return list2;
    }

    @Transactional
    public ProductPojo getByBarcode(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, id: " + barcode);
        }
        return p;
    }

    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, barcode: " + barcode);
        }
        return p;
    }

    public Boolean checkProductExists(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        if(p == null) {
            throw new ApiException(("barcode doesn't exist in products"));
        }
        return true;

    }

    @Transactional
    public ProductPojo getPrice(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        return p;
    }

    public ProductPojo getProductByName(String name) throws ApiException {
        ProductPojo productPojo = dao.getProductByName(name);
        return productPojo;
    }





    protected static void normalize(ProductPojo p) {
        p.setName(StringUtil.toLowerCase(p.getName()));
    }



}
