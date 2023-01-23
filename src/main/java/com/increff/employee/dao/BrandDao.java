package com.increff.employee.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class BrandDao extends AbstractDao {

    private static String delete_brand_by_id = "delete from BrandPojo p where id=:id";
    private static String select_brand_by_id = "select p from BrandPojo p where id=:id";
    private static String select_all_brands = "select p from BrandPojo p";

    private static String select_brand_by_name_and_category = "select p from BrandPojo p where name=:name AND category=:category";



    public void addBrand(BrandPojo p) throws ApiException {
            em().persist(p);
    }

    public int delete(int id) {
        Query query = em().createQuery(delete_brand_by_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }


    public BrandPojo getBrandById(int id) {
        TypedQuery<BrandPojo> query = getQuery(select_brand_by_id, BrandPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }

    public BrandPojo getBrand(String name, String category) {
        TypedQuery<BrandPojo> query = getQuery(select_brand_by_name_and_category, BrandPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingleBrand(query);
    }


    public List<BrandPojo> getAllBrand() {
        TypedQuery<BrandPojo> query = getQuery(select_all_brands, BrandPojo.class);

        return query.getResultList();
    }
    @Transactional
    public void update(BrandPojo p) {

    }



}
