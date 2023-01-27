package com.increff.pos.service;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DayToDayService {
    @Autowired
    private DailyReportDao dailyReportDao;
    @Transactional(rollbackOn = ApiException.class)
    public void addDailyReport(List<DailyReportPojo> dailyReports) throws ApiException {
        for(DailyReportPojo d: dailyReports) dailyReportDao.insert(d);
    }
}
