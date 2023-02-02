package com.increff.pos.dao;

import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository


public class DailyReportDao extends AbstractDao {
    private static final String SELECT_ALL_DAILY_REPORTS = "select p from DailyReportPojo p";

    public void insert(DailyReportPojo p) {
        em().persist(p);
    }

    public List<DailyReportPojo> getDailyReport() {
        TypedQuery<DailyReportPojo> query = getQuery(SELECT_ALL_DAILY_REPORTS, DailyReportPojo.class);
        return query.getResultList();
    }

}
