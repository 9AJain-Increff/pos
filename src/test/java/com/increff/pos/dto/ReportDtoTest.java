//package com.increff.pos.dto;
//
//import com.increff.pos.dao.DailyReportDao;
//import com.increff.pos.exception.ApiException;
//import com.increff.pos.model.data.DailyData;
//import com.increff.pos.pojo.BrandPojo;
//import com.increff.pos.pojo.DailyReportPojo;
//import com.increff.pos.pojo.ProductPojo;
//import com.increff.pos.service.*;
//import com.increff.pos.util.MockUtil;
//import org.junit.Before;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ReportDtoTest extends AbstractUnitTest {
//    @Autowired
//    private ReportDto reportApiDto;
//
//    @Autowired
//    private BrandService brandService;
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private InventoryService inventoryService;
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private OrderItemService orderItemService;
//
//    @Autowired
//    private DailyReportDao perDaySaleDao;
//
//    private LocalDateTime currentDate;
//
//    private List<DailyData> allPerDaySales;
//
//    @Before
//    public void setUp() throws ApiException {
//        currentDate = MockUtil.currentDate;
//        List<BrandPojo> brands = MockUtil.setUpBrands(brandService);
//        List<ProductPojo> products = MockUtil.setupProducts(brands, productService);
//        List<Integer> productIds = products.stream().map(ProductPojo::getId).collect(Collectors.toList());
//        MockUtil.setUpInventory(productIds, inventoryService);
//        MockUtil.setUpMockOrders(orderService, orderItemService, inventoryService, products);
//        allPerDaySales = Arrays.asList(
//                new DailyData("phone", "apple", 5, 540000.0),
//                new DailyData("laptop", "apple", 1, 250000.0),
//                new SalesReportData("phone", "samsung", 2, 310000.0),
//                new SalesReportData("shoe", "nike", 3, 60000.0),
//                new SalesReportData("laptop", "lenovo", 3, 195000.0));
//    }
//
//
//}
