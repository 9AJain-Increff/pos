package com.increff.pos.dto;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailyData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.util.AssertUtil;
import com.increff.pos.util.MockUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.swing.text.IconView;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.increff.pos.util.ConversionUtil.convertToBrandData;
import static com.increff.pos.util.MockUtil.BRANDS;

public class ReportDtoTest extends AbstractUnitTest {
    @Autowired
    private ReportDto reportDto;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private DailyReportDao perDaySaleDao;

    private LocalDateTime currentDate;

    private List<SalesData> allPerDaySales;

    @Before
    public void setUp() throws ApiException {
        currentDate = MockUtil.currentDate;
        List<BrandPojo> brands = MockUtil.setUpBrands(brandService);
        List<ProductPojo> products = MockUtil.setupProducts(brands, productService);
        List<Integer> productIds = products.stream().map(ProductPojo::getId).collect(Collectors.toList());
        MockUtil.setUpInventory(productIds, inventoryService);
        MockUtil.setUpMockOrders(orderService, orderItemService, inventoryService, products);
        allPerDaySales = Arrays.asList(
                new SalesData("apple", "phone", 5, 540000.0f),
                new SalesData("apple", "laptop", 1, 250000.0f),
                new SalesData("samsung", "phone", 2, 310000.0f),
                new SalesData("nike", "shoe", 3, 60000.0f),
                new SalesData("lenovo", "laptop", 3, 195000.0f));

    }

    @Test
    @Rollback
    public void allInputsNullReturnsAllCategories() throws ApiException {
        SalesForm form = new SalesForm();
        form.setStartTime(null);
        form.setEndTime(null);
        form.setBrandCategory(null);
        form.setBrandName(null);

        List<SalesData> actual = reportDto.getSalesReport(form);
        List<SalesData> expected = allPerDaySales;

        Comparator<SalesData> comparator = Comparator.comparing(SalesData::getBrandName)
                .thenComparing(SalesData::getBrandCategory);
        actual.sort(comparator);
        expected.sort(comparator);

        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualSaleReportData);
    }

    @Test
    @Rollback
    public void getBrandWiseSaleReportWhenBrandNotSpecifiedGivesReportForAllBrands() throws ApiException {
        SalesForm form = new SalesForm();

        form.setBrandCategory("");
        form.setBrandName("");
        form.setStartTime(currentDate.minusDays(2));
        form.setEndTime(currentDate);
        List<SalesData> actual = reportDto.getSalesReport(form);
        List<SalesData> expected = Arrays.asList(
                new SalesData("apple", "phone", 5, 540000.0f),
                new SalesData("apple", "laptop", 1, 250000.0f),
                new SalesData("samsung", "phone", 2, 310000.0f),
                new SalesData("nike", "shoe", 3, 60000.0f),
                new SalesData("lenovo", "laptop", 3, 195000.0f)
        );

        Comparator<SalesData> comparator = Comparator.comparing(SalesData::getBrandName)
                .thenComparing(SalesData::getBrandCategory);
        actual.sort(comparator);
        expected.sort(comparator);

        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualSaleReportData);
    }

//TODO
//    @Test
//    @Rollback
//    public void getBrandWiseSaleReportWhenOnlyBrandIsSpecifiedGivesReportForBrandsWithAllCategories() throws ApiException {
//        SalesForm form = new SalesForm();
//        form.setStartTime(currentDate.minusDays(10));
//        form.setEndTime(currentDate);
//        form.setBrandName("apple");
//        form.setBrandCategory("");
//
//        List<SalesData> actual = reportDto.getSalesReport(form);
//        List<SalesData> expected = Arrays.asList(
//                new SalesData("apple", "phone", 5, 540000.0f),
//                new SalesData("apple", "laptop", 1, 250000.0f)
//        );
//
//        AssertUtil.assertEqualList(expected, actual, AssertUtil::assertEqualSaleReportData);
//    }

    @Test(expected = ApiException.class)
    @Rollback
    public void getSalesReportThrowsApiExceptionForInvalidDates() throws ApiException {
        SalesForm salesReportForm = new SalesForm();

        salesReportForm.setStartTime(currentDate);
        salesReportForm.setEndTime(currentDate.minusDays(1));

        reportDto.getSalesReport(salesReportForm);
    }
    @Test
    @Rollback
    public void getBrandWiseSaleReportIfOnlyCategoryIsSpecifiedGivesReportForAllBrandsWithCategory() throws ApiException {
        SalesForm form = new SalesForm();
        form.setStartTime(currentDate.minusDays(10));
        form.setEndTime(currentDate);
        form.setBrandName("");
        form.setBrandCategory("phone");

        List<SalesData> actual = reportDto.getSalesReport(form);
        List<SalesData> expected = Arrays.asList(
                new SalesData("apple", "phone", 5, 540000.0f),
                new SalesData("samsung", "phone", 2, 310000.0f)
        );

        Assert.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            AssertUtil.assertEqualSaleReportData(expected.get(i), actual.get(i));
        }
    }
//    @Test
//    @Rollback
//    public void testUpdatePerDaySale() {
//        reportDto.updatePerDaySale();
//
//         form = new PerDaySaleForm();
//        LocalDateTime startDate = currentDate.minusDays(1);
//        LocalDateTime endDate = currentDate;
//
//        form.setStartDate(startDate);
//        form.setEndDate(endDate);
//
//        List<PerDaySaleData> perDaySales = reportApiDto.getPerDaySales(form);
//
//        Assert.assertEquals(1, perDaySales.size());
//
//        PerDaySaleData sale = perDaySales.get(0);
//
//        Assert.assertEquals(currentDate.minusDays(1).toLocalDate(), sale.getDate());
//        Assert.assertEquals(Integer.valueOf(2), sale.getItemsCount());
//        Assert.assertEquals(Integer.valueOf(1), sale.getOrdersCount());
//        Assert.assertEquals(Double.valueOf(435000.0), sale.getTotalRevenue());
//    }

//    @Test
//    @Rollback
//    public void getInventoryReportTest() throws ApiException {
//        List<InventoryReportData> actualInventoryReport = reportDto.getInventoryReport();
//        List<InventoryReportData> expectedInventoryReport = Arrays.asList(
//                new InventoryReportData("apple", "laptop", 9),
//                new InventoryReportData("apple", "phone", 15),
//                new InventoryReportData("lenovo", "laptop", 7),
//                new InventoryReportData("nike", "shoe", 7),
//                new InventoryReportData("samsung", "phone", 18)
//        );
//
//
//        Comparator<InventoryReportData> comparator = Comparator.comparing(InventoryReportData::getBrandName)
//                .thenComparing(InventoryReportData::getBrandCategory);
//        actualInventoryReport.sort(comparator);
//        expectedInventoryReport.sort(comparator);
//
//        Assert.assertEquals(expectedInventoryReport.size(), actualInventoryReport.size());
//
//        for (int i = 0; i < expectedInventoryReport.size(); i++) {
//            AssertUtil.assertEqualInventoryReportDate(expectedInventoryReport.get(i), actualInventoryReport.get(i));
//        }
//    }
    //TODO----check after brand service report
//    @Test
//    @Rollback
//    public void getBrandReportReturnsAllBrands() {
//        List<BrandData> actual = reportDto.getBrandReport();
//        List<BrandPojo> expected = BRANDS;
//        List<BrandData> expectedBrandData = new ArrayList<>();
//        for(BrandPojo brand: expected){
//            expectedBrandData.add(convertToBrandData(brand));
//        }
//        Comparator<BrandData> comparator = Comparator.comparing(BrandData::getName)
//                .thenComparing(BrandData::getCategory);
//        actual.sort(comparator);
//        expectedBrandData.sort(comparator);
//        AssertUtil.assertEqualList(expectedBrandData, actual, AssertUtil::assertEqualBrandData);
//
//    }


    @Test
    @Rollback
    public void testUpdatePerDaySale() throws ApiException {
        reportDto.updatePerDaySale();


        List<DailyData> perDaySales = reportDto.getDailyReport();

        Assert.assertEquals(1, perDaySales.size());

        DailyData sale = perDaySales.get(0);

        Assert.assertEquals(Integer.valueOf(6), sale.getOrderItemsQuantity());
        Assert.assertEquals(Integer.valueOf(1), sale.getOrdersQuantity());
        Assert.assertEquals(Float.valueOf(435000.0f), sale.getRevenue());
    }

}
