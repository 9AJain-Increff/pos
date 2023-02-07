package com.increff.pos.dto;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailyData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.DailyReportForm;
import com.increff.pos.model.form.SalesForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.increff.pos.util.ConversionUtil.*;
import static com.increff.pos.util.ValidationUtil.*;

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

    public List<SalesData> getSalesReport(String brandName,
            String brandCategory,
            LocalDateTime start,
            LocalDateTime end) throws ApiException {
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
            brandIdToQuantityMapping.put(brand.getId(),brandIdToQuantityMapping.getOrDefault(brand.getId(),0)+inventory.getQuantity());
        }
        return getInventoryRportdata(brandIdToQuantityMapping, brandIdToBrandMapping,form);
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


    public List<DailyData> getDailyReport(DailyReportForm form) throws ApiException {
        LocalDateTime startTime = getStartTime(form.getStartTime());
        LocalDateTime endTime = getEndTime(form.getEndTime());
        if(startTime.isAfter(endTime)){
            throw new ApiException("start time should be before end time");
        }
        List<DailyReportPojo> dailyReportPojo = dayToDayService.getDailyReport();
        List<DailyData> dailyDataList = new ArrayList<>();
        for (DailyReportPojo d : dailyReportPojo) {
            LocalDateTime date = d.getDate();
            if(timeIsBetween(date, startTime, endTime)){
                dailyDataList.add(convertToDailyData(d));
            }
        }
        return dailyDataList;
    }

    public void updatePerDaySale() {
        LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime yesterday = today.minusDays(1);
        List<OrderPojo> orders = orderService.getOrdersBetweenTime(yesterday, today);
        Map<LocalDateTime, Integer> dateToOrdersQuantity = new HashMap<>();
        Map<LocalDateTime, Integer> dateToOrderItemsQuantity = new HashMap<>();
        Map<LocalDateTime, Float> dateToRevenue = new HashMap<>();
        for (OrderPojo order : orders) {
            LocalDateTime time = order.getCreatedOn();
            dateToOrdersQuantity.put(time, dateToOrdersQuantity.getOrDefault(time, 0) + 1);
            List<OrderItemPojo> orderItems = orderItemService.getOrderItemsById(order.getId());
            for (OrderItemPojo orderItem : orderItems) {
                dateToRevenue.put(time,dateToRevenue.getOrDefault(time,0.0f)+orderItem.getQuantity() * orderItem.getSellingPrice());
                dateToOrderItemsQuantity.put(time,dateToOrderItemsQuantity.getOrDefault(time,0)+ orderItem.getQuantity());
            }

        }
        List<DailyReportPojo> dailyReportPojo = updateDailyData(dateToOrdersQuantity, dateToOrderItemsQuantity, dateToRevenue);
        dayToDayService.addDailyReport(dailyReportPojo);
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

    private boolean timeIsBetween(LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate) {
        if (date.isEqual(startDate) || date.isEqual(endDate)) return true;
        return date.isBefore(endDate) && date.isAfter(startDate);
    }


    private List<DailyReportPojo> updateDailyData(
            Map<LocalDateTime, Integer> dateToOrdersQuantity,
            Map<LocalDateTime, Integer> dateToOrderItemsQuantity,
            Map<LocalDateTime, Float> dateToRevenue) {
        Iterator<Map.Entry<LocalDateTime, Integer>> itr = dateToOrdersQuantity.entrySet().iterator();
        List<DailyReportPojo> dailyReportPojoList = new ArrayList<>();
        while (itr.hasNext()) {
            Map.Entry<LocalDateTime, Integer> entry = itr.next();
            DailyReportPojo dailyReportPojo = new DailyReportPojo();
            LocalDateTime time = entry.getKey();
            int quantity = entry.getValue();
            dailyReportPojo.setOrdersQuantity(quantity);
            dailyReportPojo.setOrderItemsQuantity(dateToOrderItemsQuantity.get(time));
            dailyReportPojo.setRevenue(dateToRevenue.get(time));
            dailyReportPojo.setDate(time);
            dailyReportPojoList.add(dailyReportPojo);
        }
        return dailyReportPojoList;
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


}
