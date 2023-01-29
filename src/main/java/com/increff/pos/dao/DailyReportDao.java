package com.increff.pos.dao;

import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
/**
 * Todo Missed follwoing points which are mentioned in the document
 * https://increff.atlassian.net/wiki/spaces/TB/pages/312377489/Java+Class+layering+and+Structure#Purpose.7
 * No checked exception should be thrown from DAO Layer
 * At the class level, one should have '@Transactional' instead of the method level
 */

// TODO: 29/01/23 extends AbstractDao?
public class DailyReportDao {
    @PersistenceContext
    private EntityManager em;

    // TODO: 29/01/23 why this method is throwing ApiException?
    public void insert(DailyReportPojo p) throws ApiException {
        em.persist(p);
    }
}
