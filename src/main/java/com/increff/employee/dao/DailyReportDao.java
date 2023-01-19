package com.increff.employee.dao;

import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.DailyReportPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class DailyReportDao {
    @PersistenceContext
    private EntityManager em;
    public void insert(DailyReportPojo p) throws ApiException {
        em.persist(p);
    }
}
