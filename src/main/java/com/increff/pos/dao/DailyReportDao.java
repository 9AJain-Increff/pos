package com.increff.pos.dao;

import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DailyReportDao {
    @PersistenceContext
    private EntityManager em;

    public void insert(DailyReportPojo p) throws ApiException {
        em.persist(p);
    }
}
