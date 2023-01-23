package com.increff.employee.service;

import com.increff.employee.dao.DailyReportDao;
import com.increff.employee.model.*;
import com.increff.employee.pojo.*;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.employee.util.ConversionUtil.convertToBrandData;

@Service
public class ReportService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private DailyReportDao dailyReportDao;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
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

        Map<Integer, ProductPojo> barcodeToProductMapping =  productService.getProductsByProductIds(productIds);
        Map<Integer,Integer> barcodeToQuantityMapping = getQuantityMapping(filteredOrderItems);

        List<SalesData> salesData = new ArrayList<>();
        for (Map.Entry<Integer, ProductPojo> entry : barcodeToProductMapping.entrySet()) {
            Integer productId = entry.getKey();
            ProductPojo product = entry.getValue();
            BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
            SalesData saleData = new SalesData();
            saleData.setQuantity(barcodeToQuantityMapping.get(productId));
            saleData.setBrandCategory(brand.getCategory());
            saleData.setBrandName(brand.getName());
            saleData.setRevenue(barcodeToQuantityMapping.get(productId) * product.getPrice());
            saleData.setProductId(productId);
            salesData.add(saleData);
        }

        return salesData;

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
    @Transactional
    public List<DailyReportPojo> getDailyReport() throws ApiException {
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
                    dateToRevenue.put(order.getCreatedOn().toLocalDate(), dateToRevenue.get(order.getCreatedOn().toLocalDate()) + orderItem.getQuantity()*orderItem.getPrice());
                }
                else{
                    dateToOrderItemsQuantity.put(order.getCreatedOn().toLocalDate(), orderItem.getQuantity());
                    dateToRevenue.put(order.getCreatedOn().toLocalDate(), orderItem.getQuantity()*orderItem.getPrice());
                }
            }

        }
        List<DailyReportPojo> dailyReportPojo = getDailyData(dateToOrdersQuantity,dateToOrderItemsQuantity,dateToRevenue);

        for(DailyReportPojo d: dailyReportPojo){
            dailyReportDao.insert(d);
        }
        return dailyReportPojo;
    }

}
