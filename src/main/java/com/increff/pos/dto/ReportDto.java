package com.increff.pos.dto;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailyData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.BarcodeForm;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.DailyReportForm;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.pos.util.ConversionUtil.*;
import static com.increff.pos.util.DateTimeUtil.formatEndDate;
import static com.increff.pos.util.DateTimeUtil.formatStartDate;
import static com.increff.pos.util.ValidationUtil.isBlank;

@Component
public class ReportDto {

    @Autowired
    private DailyReportDao dailyReportDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private DayToDayService dayToDayService;

    private Boolean checkValidity(BrandPojo brand, String brandName, String brandCategory) {
        if (isBlank(brandName) && isBlank(brandCategory)) {
            return true;
        } else if (isBlank(brandName)&& brandCategory.equals(brand.getCategory())) {
            return true;
        } else if (brandName.equals(brand.getName()) && isBlank(brandCategory)) {
            return true;
        } else if (brandName.equals(brand.getName()) && brandCategory.equals(brand.getCategory())) {
            return true;
        } else return false;
    }

    private List<OrderItemPojo> filterOrderItems(List<OrderItemPojo> orderItems, String brandName, String brandCategory) throws ApiException {
        List<OrderItemPojo> filteredOrderItems = new ArrayList<>();
        for (OrderItemPojo orderItem : orderItems) {
            ProductPojo product = productService.checkProduct(orderItem.getProductId());
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            if (checkValidity(brand, brandName, brandCategory)) {
                filteredOrderItems.add(orderItem);
            }
        }
        return filteredOrderItems;
    }

    public List<SalesData> getSalesReport(String brandName,
            String brandCategory,
            Date start,
            Date end) throws ApiException {
        LocalDateTime startTime = getStartTime(start);
        LocalDateTime endTime = getEndTime(end);
        if(brandName == (null))brandName = "";
        if(brandCategory==(null))brandCategory = "";
        if(startTime.isAfter(endTime)){
            throw new ApiException("start time should be before end time");
        }
        List<OrderPojo> orders = orderService.getOrdersBetweenTime(startTime, endTime);
        List<OrderItemPojo> orderItems = orderItemService.getOrderItemByOrders(orders);
        List<OrderItemPojo> filteredOrderItems = filterOrderItems(orderItems, brandName, brandCategory);
        Map<Integer, BrandPojo> brandIdToBrandMapping = new HashMap<>();
        Map<Integer, Integer> brandIdToQuantityMapping = new HashMap<>();
        Map<Integer, Float> brandIdToRevenueMapping = new HashMap<>();
        for(OrderItemPojo orderItemPojo : filteredOrderItems){
            ProductPojo product = productService.getProductById(orderItemPojo.getProductId());
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            brandIdToBrandMapping.put(brand.getId(),brand);
            brandIdToQuantityMapping.put(brand.getId(),brandIdToQuantityMapping.getOrDefault(brand.getId(),0)+orderItemPojo.getQuantity());
            brandIdToRevenueMapping.put(brand.getId(),brandIdToRevenueMapping.getOrDefault(brand.getId(),0.0f)+orderItemPojo.getSellingPrice()*orderItemPojo.getQuantity());
        }
        Iterator<Map.Entry<Integer, Integer>> iterator = brandIdToQuantityMapping.entrySet().iterator();
        List<SalesData> salesData = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            Integer brandId = entry.getKey();
            SalesData sales = new SalesData();
            sales.setRevenue(brandIdToRevenueMapping.get(brandId));
            sales.setQuantity(brandIdToQuantityMapping.get(brandId));
            sales.setBrandName(brandIdToBrandMapping.get(brandId).getName());
            sales.setBrandCategory(brandIdToBrandMapping.get(brandId).getCategory());
            salesData.add(sales);
        }
    return salesData;
    }

    public List<SalesData> getSalesReport(SalesForm form) throws ApiException {

        List<SalesData> salesReport = getSalesReport(form.getBrandName(), form.getBrandCategory(),
                form.getStartTime(), form.getEndTime());
        return salesReport;
    }

    public List<InventoryReportData> getInventoryReport(BrandForm form) throws ApiException {

        List<InventoryPojo> inventoryPojoList = inventoryService.getAllInventory();
        List<Integer> productIds = new ArrayList<>();
        for (InventoryPojo inventory : inventoryPojoList) {
            productIds.add(inventory.getProductId());
        }
        List<ProductPojo> products = productService.getProductsByIds(productIds);
        Map<Integer, ProductPojo> productIdToProductMapping = new HashMap<>();
        for(ProductPojo product : products){
            productIdToProductMapping.put(product.getId(),product);
        }
        Map<Integer, Integer> brandIdToQuantityMapping = new HashMap<>();
        Map<Integer, BrandPojo> brandIdToBrandMapping = new HashMap<>();
        for (InventoryPojo inventory : inventoryPojoList) {
            ProductPojo product = productIdToProductMapping.get(inventory.getProductId());
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            brandIdToBrandMapping.put(brand.getId(), brand);
            if (brandIdToQuantityMapping.containsKey(brand.getId()))
                brandIdToQuantityMapping.put(brand.getId(), brandIdToQuantityMapping.get(brand.getId()) + inventory.getQuantity());
            else
                brandIdToQuantityMapping.put(brand.getId(), inventory.getQuantity());
        }
        return getInventoryRportdata(brandIdToQuantityMapping, brandIdToBrandMapping,form);
    }

    private List<InventoryReportData> getInventoryRportdata(Map<Integer, Integer> brandIdToQuantityMapping,
                                                            Map<Integer, BrandPojo> brandIdToBrandMapping,
                                                            BrandForm form) {
        Iterator<Map.Entry<Integer, Integer>> iterator = brandIdToQuantityMapping.entrySet().iterator();
        List<InventoryReportData> inventoriesReportData = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            BrandPojo brand = brandIdToBrandMapping.get(entry.getKey());
            if(checkValidity(brand,form.getName(),form.getCategory())) {
                InventoryReportData inventoryReportData = new InventoryReportData();
                inventoryReportData.setQuantity(entry.getValue());
                inventoryReportData.setBrandName(brandIdToBrandMapping.get(entry.getKey()).getName());
                inventoryReportData.setBrandCategory(brandIdToBrandMapping.get(entry.getKey()).getCategory());
                inventoriesReportData.add(inventoryReportData);
            }
        }
        return inventoriesReportData;
    }

    public List<BrandData> getBrandReport(BrandForm form) {
        List<BrandPojo> brands = brandService.getAllBrand();
        List<BrandData> brandsData = new ArrayList<>();
        for(BrandPojo brand : brands){
            if(checkValidity(brand, form.getName(), form.getCategory())){
                brandsData.add(convertToBrandData(brand));
            }
        }
        return brandsData;
    }

    private List<DailyReportPojo> updateDailyData(
            Map<LocalDate, Integer> dateToOrdersQuantity,
            Map<LocalDate, Integer> dateToOrderItemsQuantity,
            Map<LocalDate, Float> dateToRevenue) {
        Iterator<Map.Entry<LocalDate, Integer>> itr = dateToOrdersQuantity.entrySet().iterator();
        List<DailyReportPojo> dailyReportPojoList = new ArrayList<>();
        while (itr.hasNext()) {
            Map.Entry<LocalDate, Integer> entry = itr.next();
            DailyReportPojo dailyReportPojo = new DailyReportPojo();
            LocalDate time = entry.getKey();
            int quantity = entry.getValue();
            dailyReportPojo.setOrdersQuantity(quantity);
            dailyReportPojo.setOrderItemsQuantity(dateToOrderItemsQuantity.get(time));
            dailyReportPojo.setRevenue(dateToRevenue.get(time));
            dailyReportPojo.setDate(time);
            dailyReportPojoList.add(dailyReportPojo);


        }
        return dailyReportPojoList;
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    private LocalDateTime getStartTime(Date start){
        LocalDateTime startTime = LocalDateTime.MIN;
        if(start== null){
            startTime = formatStartDate(startTime);
        }
        else {
            startTime = (convertToLocalDateTimeViaInstant(start));
            startTime = formatStartDate(startTime);
        }
        return startTime;
    }

    private LocalDateTime getEndTime(Date end){
        LocalDateTime endTime = LocalDateTime.MAX;

        if(end== null){
            endTime= formatEndDate(endTime);
        }
        else {
            endTime = (convertToLocalDateTimeViaInstant(end));
            endTime = formatEndDate(endTime);
        }
        return endTime;
    }

    private boolean isBetween(LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate) {
        if (date.isEqual(startDate) || date.isEqual(endDate)) return true;
        return date.isBefore(endDate) && date.isAfter(startDate);
    }
    public List<DailyData> getDailyReport(DailyReportForm form) throws ApiException {
        Date start = form.getStartTime(), end = form.getEndTime();
        LocalDateTime startTime = getStartTime(start);
        LocalDateTime endTime = getEndTime(end);
        if(startTime.isAfter(endTime)){
            throw new ApiException("start time should be before end time");
        }
        List<DailyReportPojo> dailyReportPojo = dayToDayService.getDailyReport();
        List<DailyData> dailyDataList = new ArrayList<>();
        for (DailyReportPojo d : dailyReportPojo) {
            LocalDateTime date = d.getDate().atStartOfDay();
            if(isBetween(date, startTime, endTime)){
                dailyDataList.add(convertToDailyData(d));
            }
        }
        return dailyDataList;
    }

    public void updatePerDaySale() {
        // TODO: 29/01/23 you can populate all 3 maps at a time
        LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime yesterday = today.minusDays(1);
        List<OrderPojo> orders = orderService.getOrdersBetweenTime(yesterday, today);
        Map<LocalDate, Integer> dateToOrdersQuantity = new HashMap<>();
        Map<LocalDate, Integer> dateToOrderItemsQuantity = new HashMap<>();
        Map<LocalDate, Float> dateToRevenue = new HashMap<>();

        for (OrderPojo order : orders) {
            // todo use getOrDefault
            dateToOrdersQuantity.put(order.getCreatedOn().toLocalDate(), dateToOrdersQuantity.getOrDefault(order.getCreatedOn().toLocalDate(), 0) + 1);

            List<OrderItemPojo> orderItems = orderItemService.getOrderItemsById(order.getId());
            for (OrderItemPojo orderItem : orderItems) {
                if (dateToRevenue.containsKey(order.getCreatedOn().toLocalDate())) {
                    dateToOrderItemsQuantity.put(order.getCreatedOn().toLocalDate(), dateToOrderItemsQuantity.get(order.getCreatedOn().toLocalDate()) + orderItem.getQuantity());
                    dateToRevenue.put(order.getCreatedOn().toLocalDate(), dateToRevenue.get(order.getCreatedOn().toLocalDate()) + orderItem.getQuantity() * orderItem.getSellingPrice());
                } else {
                    dateToOrderItemsQuantity.put(order.getCreatedOn().toLocalDate(), orderItem.getQuantity());
                    dateToRevenue.put(order.getCreatedOn().toLocalDate(), orderItem.getQuantity() * orderItem.getSellingPrice());
                }
            }

        }
        List<DailyReportPojo> dailyReportPojo = updateDailyData(dateToOrdersQuantity, dateToOrderItemsQuantity, dateToRevenue);
        dayToDayService.addDailyReport(dailyReportPojo);

    }


}
