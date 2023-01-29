package com.increff.pos.dto;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.pos.util.ConversionUtil.convertToBrandData;
import static com.increff.pos.util.ConversionUtil.convertToDailyData;

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

    private Boolean checkValidity(BrandPojo brand,String brandName, String brandCategory){
        if(brandName.isEmpty() && brandCategory.isEmpty() ){
            return true;
        }
        else if(brandName.isEmpty() && brandCategory.equals(brand.getCategory())){
            return true;
        }
        else if(brandName.equals( brand.getName()) && brandCategory.isEmpty() ){
            return true;
        }
        else if(brandName.equals(brand.getName())  && brandCategory.equals( brand.getCategory()) ){
            return true;
        }
        else return false;
    }
    private List<OrderItemPojo> filterOrderItems(List<OrderItemPojo> orderItems , String brandName, String brandCategory) throws ApiException {
        List<OrderItemPojo> filteredOrderItems = new ArrayList<>();
        for(OrderItemPojo orderItem: orderItems){
            ProductPojo product = productService.getProductById(orderItem.getProductId());
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            if(checkValidity(brand, brandName, brandCategory)){
                filteredOrderItems.add(orderItem);
            }
        }
        return filteredOrderItems;
    }
    private Map<Integer,Integer> getQuantityMapping(List<OrderItemPojo> orderItems){
        Map<Integer, Integer> mapping = new HashMap<>();
        for(OrderItemPojo orderItem : orderItems){
            Integer productId =orderItem.getProductId();
            if(mapping.containsKey(productId)){
                mapping.put(productId, mapping.get(productId)+orderItem.getQuantity());
            }
            else{
                mapping.put(productId,orderItem.getQuantity());
            }
        }
        return mapping;
    }
    private Map<Integer,Integer> getQuantityMapping(List<Integer> productIds,List<OrderItemPojo> orderItems ) throws ApiException {
        Map<Integer, Integer> mapping = new HashMap<>();
        for(int index =0;index<productIds.size();index++){
            Integer productId = productIds.get(index);
            ProductPojo product = productService.getProductById(productId);
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            OrderItemPojo orderItem = orderItems.get(index);
            if(mapping.containsKey(brand.getId())){
                mapping.put(brand.getId(), mapping.get(brand.getId())+orderItem.getQuantity());
            }
            else{
                mapping.put(brand.getId(),orderItem.getQuantity());
            }
        }
        return mapping;
    }
    private Map<Integer,Float> getRevenueMapping(List<OrderItemPojo> orderItems) throws ApiException {
        Map<Integer,Float> brandToRevenueMapping = new HashMap<>();
        for(OrderItemPojo orderItem : orderItems){
            ProductPojo product = productService.getProductById(orderItem.getProductId());
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            if(brandToRevenueMapping.containsKey(brand.getId())){
                brandToRevenueMapping.put(brand.getId(), brandToRevenueMapping.get(brand.getId())+orderItem.getSellingPrice());
            }
            else{
                brandToRevenueMapping.put(brand.getId(),orderItem.getSellingPrice());
            }
        }
        return brandToRevenueMapping;
    }
    public List<SalesData> getSalesReport(
            String brandName,
            String brandCategory,
            Date startTime,
            Date endTime) throws ApiException {

        List<OrderPojo> orders = orderService.getOrdersBetweenTime(startTime, endTime);
        List<OrderItemPojo> orderItems = orderItemService.getOrderItemByOrders(orders);
        List<OrderItemPojo> filteredOrderItems= filterOrderItems(orderItems,brandName, brandCategory);
        List<Integer> productIds =    filteredOrderItems.stream()
                .map(OrderItemPojo::getProductId)
                .collect(Collectors.toList());

        Map<Integer, Integer> brandIdToQuantityMapping = getQuantityMapping(productIds,filteredOrderItems);
        Map<Integer,Float> brandIdToRevenueMapping = getRevenueMapping(filteredOrderItems);
        Iterator<Map.Entry<Integer, Integer>> iterator = brandIdToQuantityMapping.entrySet().iterator();
        List<SalesData> salesData = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            Integer brandId = entry.getKey();
            SalesData sales = new SalesData();
            BrandPojo brand = brandService.getAndCheckBrandById(brandId);
            sales.setRevenue(brandIdToRevenueMapping.get(brandId));
            sales.setQuantity(brandIdToQuantityMapping.get(brandId));
            sales.setBrandName(brand.getName());
            sales.setBrandCategory(brand.getCategory());
            salesData.add(sales);
        }
        return  salesData;
    }
    public List<SalesData> getSalesReport(SalesForm form) throws ApiException {


        List<SalesData> salesReport = getSalesReport(form.getBrandName(), form.getBrandCategory(),
                form.getStartTime(), form.getEndTime());
        return salesReport;
    }
    public List<InventoryReportData> getInventoryReport() throws ApiException {

        List<InventoryPojo> inventoryPojoList = inventoryService.getAllInventory();
        List<Integer> productIds =  new ArrayList<>();
        for(InventoryPojo inventory: inventoryPojoList){
            productIds.add(inventory.getProductId());
        }
        Map<Integer, ProductPojo> productIdToProductMapping =  productService.getProductsByProductIds(productIds);
        List<InventoryReportData> inventoriesReportData = new ArrayList<>();

        for(InventoryPojo inventory : inventoryPojoList){
            InventoryReportData inventoryReportData = new InventoryReportData();
            inventoryReportData.setQuantity(inventory.getQuantity());
            ProductPojo product = productIdToProductMapping.get(inventory.getProductId());
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            inventoryReportData.setBrandCategory(brand.getCategory());
            inventoryReportData.setBrandName(brand.getName());
            inventoriesReportData.add(inventoryReportData);
        }
        return inventoriesReportData;
    }



    public List<BrandData> getBrandReport() throws ApiException {
        List<BrandPojo> brands = brandService.getAllBrand();
        List<BrandData> brandsData = new ArrayList<BrandData>();
        for (BrandPojo brandPojo : brands) {
            brandsData.add(convertToBrandData(brandPojo));
        }
        return brandsData;
    }

    private List<DailyReportPojo> getDailyData(
            Map<LocalDate, Integer> dateToOrdersQuantity,
            Map<LocalDate, Integer> dateToOrderItemsQuantity,
            Map<LocalDate, Float> dateToRevenue){
        Iterator<Map.Entry<LocalDate, Integer>> itr = dateToOrdersQuantity.entrySet().iterator();
        List<DailyReportPojo> dailyReportPojoList = new ArrayList<>();
        while(itr.hasNext())
        {
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
    public List<DailyData> getDailyReport() throws ApiException {
        List<OrderPojo> orders = orderService.getAllOrders();
        Map<LocalDate, Integer> dateToOrdersQuantity = new HashMap<>();
        Map<LocalDate, Integer> dateToOrderItemsQuantity = new HashMap<>();
        Map<LocalDate, Float> dateToRevenue = new HashMap<>();

        for(OrderPojo order: orders){
            if(dateToOrdersQuantity.containsKey(order.getCreatedOn().toLocalDate()  )) {
                dateToOrdersQuantity.put(order.getCreatedOn().toLocalDate(), dateToOrdersQuantity.get(order.getCreatedOn().toLocalDate()) + 1);
            }
            else{
                dateToOrdersQuantity.put(order.getCreatedOn().toLocalDate(), 1);
            }

            List<OrderItemPojo> orderItems = orderItemService.getOrderItemsById(order.getId());
            for(OrderItemPojo orderItem: orderItems){
                if(dateToRevenue.containsKey(order.getCreatedOn().toLocalDate())) {
                    dateToOrderItemsQuantity.put(order.getCreatedOn().toLocalDate(), dateToOrderItemsQuantity.get(order.getCreatedOn().toLocalDate()) + orderItem.getQuantity());
                    dateToRevenue.put(order.getCreatedOn().toLocalDate(), dateToRevenue.get(order.getCreatedOn().toLocalDate()) + orderItem.getQuantity()*orderItem.getSellingPrice());
                }
                else{
                    dateToOrderItemsQuantity.put(order.getCreatedOn().toLocalDate(), orderItem.getQuantity());
                    dateToRevenue.put(order.getCreatedOn().toLocalDate(), orderItem.getQuantity()*orderItem.getSellingPrice());
                }
            }

        }
        List<DailyReportPojo> dailyReportPojo = getDailyData(dateToOrdersQuantity,dateToOrderItemsQuantity,dateToRevenue);

        dayToDayService.addDailyReport(dailyReportPojo);

        List<DailyData> dailyData = new ArrayList<>();
        for(DailyReportPojo d: dailyReportPojo){
            dailyData.add(convertToDailyData(d));
        }
        return dailyData;
    }



}
