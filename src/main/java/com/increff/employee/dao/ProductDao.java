package com.increff.employee.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDao extends AbstractDao {

    private static String delete_barcode = "delete from ProductPojo p where barcode=:barcode";
    private static String select_barcode = "select p from ProductPojo p where barcode=:barcode";
    private static String select_product_by_id = "select p from ProductPojo p where id=:id";

    private static String select_name = "select p from ProductPojo p where name=:name , brandName=:brandName AND brandCategory=:brandCategory";
    private static String select_id = "select p from ProductPojo p where id=:id";
    private static String select_all = "select p from ProductPojo p";

    private static String select_product = "select p from ProductPojo p where name=:name AND category=:category";
    private static String check = "select p from ProductPojo p where barcode=:barcode";
    private static String select_product_by_name = "select p from ProductPojo p where name=:name";
    @PersistenceContext
    private EntityManager em;

    public void add(ProductPojo p) throws ApiException {
        em.persist(p);
    }

//    public int delete(String barcode) {
//        Query query = em.createQuery(delete_barcode);
//        query.setParameter("barcode", barcode);
//        return query.executeUpdate();
//    }


    public ProductPojo checkProductExists(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(check, ProductPojo.class);
        query.setParameter("barcode",barcode);
        ProductPojo p = getSingleBrand(query);
        return p;
    }
    public ProductPojo getProductByBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(select_barcode, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    public ProductPojo getProductById(Integer id) {
        TypedQuery<ProductPojo> query = getQuery(select_product_by_id, ProductPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }

    public ProductPojo getProductByBrandName(
            String name,
            String brandName,
            String brandCategory) {
        TypedQuery<ProductPojo> query = getQuery(select_name, ProductPojo.class);
        query.setParameter("name", name);
        query.setParameter("brandName", brandName);
        query.setParameter("brandCategory", brandCategory);
        System.out.println(query);
        return getSingleBrand(query);
    }

    public ProductPojo select(int id) {
        TypedQuery<ProductPojo> query = getQuery(select_id, ProductPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }


    public ProductPojo select(String name, String category) {
        TypedQuery<ProductPojo> query = getQuery(select_product, ProductPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingleBrand(query);
    }

    public List<ProductPojo> getAllProduct() {
        TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class);

        return query.getResultList();
    }
    public ProductPojo getProductByProductName(ProductPojo product) {
        TypedQuery<ProductPojo> query = getQuery(select_product_by_name, ProductPojo.class);
        query.setParameter("name", product.getName());
        return getSingleBrand(query);
    }

    @Transactional
    public void update(ProductPojo p) {

    }



}
