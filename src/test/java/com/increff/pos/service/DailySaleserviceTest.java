//package com.increff.pos.service;
//
//import com.increff.pos.dao.DailyReportDao;
//import org.junit.Before;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDateTime;
//import java.util.LinkedList;
//
//public class DailySaleserviceTest extends AbstractUnitTest{
//    @Autowired
//    private PerDaySaleService perDaySaleService;
//
//    @Autowired
//    public DailyReportDao dailyReportDao;
//
//    private List<PerDaySale> perDaySaleList;
//
//    private LocalDateTime currentDate;
//
//    @Before
//    public void setUp() {
//        currentDate = LocalDateTime.now();
//        perDaySaleList = new LinkedList<>();
//
//        for (int i = 0; i < 5; i++) {
//            PerDaySale perDaySale = MockUtil.getMockPerDaySale();
//            perDaySale.setDate(currentDate.plusDays(i));
//            perDaySaleDao.insert(perDaySale);
//            perDaySaleList.add(perDaySale);
//        }
//    }
//
//}
