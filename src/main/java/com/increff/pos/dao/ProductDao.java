package com.increff.pos.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Todo
 * Missed follwoing 2 points which are mentioned in the document
 * https://increff.atlassian.net/wiki/spaces/TB/pages/312377489/Java+Class+layering+and+Structure#Purpose.7
 * No checked exception should be thrown from DAO Layer
 * At the class level, one should have '@Transactional' instead of the method level
 *
 * todo Validate the queries and remove unnecessary methods
 */
public class ProductDao extends AbstractDao {

    private static String select_product_by_barcode = "select p from ProductPojo p where barcode=:barcode";
    private static String select_product_by_id = "select p from ProductPojo p where id=:id";
    private static String select_all = "select p from ProductPojo p";


    public ProductPojo add(ProductPojo p) throws ApiException {
        em().persist(p);
        return p;
    }

    public ProductPojo getProductByBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(select_product_by_barcode, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    public ProductPojo getProductById(Integer id) {
        TypedQuery<ProductPojo> query = getQuery(select_product_by_id, ProductPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }

    public List<ProductPojo> getAllProduct() {
        TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class);
        return query.getResultList();
    }
}
