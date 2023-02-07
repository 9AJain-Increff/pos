package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import com.increff.pos.pojo.BrandPojo;
import org.springframework.stereotype.Repository;

@Repository

public class BrandDao extends AbstractDao {
    private static final String SELECT_BRAND_BY_ID = "select p from BrandPojo p where id=:id";
    private static final String SELECT_ALL_BRANDS = "select p from BrandPojo p";
    private static final String SELECT_BRAND_BY_NAME_AND_CATEGORY = "select p from BrandPojo p where name=:name AND category=:category";

    public void addBrand(BrandPojo p) {
        em().persist(p);
    }

    public BrandPojo getBrandById(Integer id) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BRAND_BY_ID, BrandPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public BrandPojo getBrandByNameAndCategory(String name, String category) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BRAND_BY_NAME_AND_CATEGORY, BrandPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingle(query);
    }


    public List<BrandPojo> getAllBrand() {
        TypedQuery<BrandPojo> query = getQuery(SELECT_ALL_BRANDS, BrandPojo.class);

        return query.getResultList();
    }


}
