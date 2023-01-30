package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

@Repository

public class ProductDao extends AbstractDao {

    private static final String SELECT_PRODUCT_BY_BARCODE = "select p from ProductPojo p where barcode=:barcode";
    private static final String SELECT_PRODUCT_BY_ID = "select p from ProductPojo p where id=:id";
    private static final String SELECT_ALL = "select p from ProductPojo p";


    public ProductPojo add(ProductPojo p)  {
        em().persist(p);
        return p;
    }

    public ProductPojo getProductByBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_PRODUCT_BY_BARCODE, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    public ProductPojo getProductById(Integer id) {
        TypedQuery<ProductPojo> query = getQuery(SELECT_PRODUCT_BY_ID, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<ProductPojo> getAllProduct() {
        TypedQuery<ProductPojo> query = getQuery(SELECT_ALL, ProductPojo.class);
        return query.getResultList();
    }
}
