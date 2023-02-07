package com.increff.pos.service;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class DailySaleserviceTest extends AbstractUnitTest{
    @Autowired
    private DayToDayService perDaySaleService;

    @Autowired
    public DailyReportDao dailyReportDao;

    private List<DailyReportPojo> perDaySaleList;

    private LocalDateTime currentDate;

    @Before
    public void setUp() {
        currentDate = LocalDateTime.now();
        perDaySaleList = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            DailyReportPojo perDaySale = MockUtil.getMockPerDaySale();
            perDaySale.setDate(LocalDateTime.from(currentDate.plusDays(i)));
            dailyReportDao.insert(perDaySale);
            perDaySaleList.add(perDaySale);
        }
    }
    @Test
    public void addPerDaySale() {
        perDaySaleService.addDailyReport(perDaySaleList);
        List<DailyReportPojo> actual = dailyReportDao.getDailyReport();
        AssertUtil.assertEqualList(perDaySaleList, actual,AssertUtil::assertEqualPerDaySale);
    }

    @Test
    public void getAllPerDaySale() {
        List<DailyReportPojo> actual = perDaySaleService.getDailyReport();
        List<DailyReportPojo> expected = perDaySaleList;
        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualPerDaySale);
    }

}
