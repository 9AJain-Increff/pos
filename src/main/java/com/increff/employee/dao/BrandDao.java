package com.increff.employee.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

import com.increff.employee.pojo.EmployeePojo;

@Repository
public class BrandDao extends AbstractDao {

    private static String delete_id = "delete from BrandPojo p where id=:id";
    private static String select_id = "select p from BrandPojo p where id=:id";
    private static String select_all = "select p from BrandPojo p";

    private static String select_brand = "select p from BrandPojo p where name=:name AND category=:category";
    private static String check = "select p from BrandPojo p where name=:name AND category=:category";
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(BrandPojo p) throws ApiException {

            em.persist(p);


    }

    public int delete(int id) {
        Query query = em().createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }


    public Boolean checkBrandExists(String name, String category) {
        TypedQuery<BrandPojo> query = getQuery(check, BrandPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        BrandPojo p = getSingle(query);

        if(p==null){return true;}
        else{
            return false;
        }
    }
    public BrandPojo select(int id) {
        TypedQuery<BrandPojo> query = getQuery(select_id, BrandPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public BrandPojo select(String name, String category) {
        TypedQuery<BrandPojo> query = getQuery(select_brand, BrandPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingle(query);
    }

    public List<BrandPojo> selectAll() {
        TypedQuery<BrandPojo> query = getQuery(select_all, BrandPojo.class);

        return query.getResultList();
    }
    @Transactional
    public void update(BrandPojo p) {

    }



}
