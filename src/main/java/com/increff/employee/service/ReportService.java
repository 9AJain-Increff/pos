package com.increff.employee.service;

import com.increff.employee.dto.ReportDto;
import com.increff.employee.model.*;
import com.increff.employee.pojo.*;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.employee.util.ConversionUtil.convertToBrandData;

@Service
public class ReportService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    private Map<String,Integer> getQuantityMapping(List<OrderItemPojo> orderItems){
        Map<String, Integer> mapping = new HashMap<>();
        for(OrderItemPojo orderItem : orderItems){
            String barcode =orderItem.getBarcode();
            if(mapping.containsKey(barcode)){
                mapping.put(barcode, mapping.get(barcode)+orderItem.getQuantity());
            }
            else{
                mapping.put(barcode,orderItem.getQuantity());
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
        for(OrderItemPojo order: orderItems){
            ProductPojo product = productService.getProductByBarcode(order.getBarcode());
            BrandPojo brand = brandService.getBrandById(product.getBrandId());
            if(checkValidity(brand, brandName, brandCategory)){
                filteredOrderItems.add(order);
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
        List<String> barcodes =    filteredOrderItems.stream()
                .map(OrderItemPojo::getBarcode)
                .collect(Collectors.toList());

        Map<String, ProductPojo> barcodeToProductMapping =  productService.getProductsBybarcodes(barcodes);
        Map<String,Integer> barcodeToQuantityMapping = getQuantityMapping(filteredOrderItems);

        List<SalesData> salesData = new ArrayList<>();
        Iterator<Map.Entry<String, ProductPojo>> itr = barcodeToProductMapping.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, ProductPojo> entry = itr.next();
            String barcode = entry.getKey();
            ProductPojo product = entry.getValue();
            BrandPojo brand = brandService.getBrandById(product.getBrandId());
            SalesData saleData = new SalesData();
            saleData.setQuantity(barcodeToQuantityMapping.get(barcode));
            saleData.setBrandCategory(brand.getCategory());
            saleData.setBrandName(brand.getName());
            saleData.setRevenue(barcodeToQuantityMapping.get(barcode)*product.getPrice());
            salesData.add(saleData);
        }

        return salesData;

    }

    public List<InventoryReportData> getInventoryReport() throws ApiException {

       List<InventoryPojo> inventoryPojoList = inventoryService.getAllInventory();
       List<String> barcodes =    inventoryPojoList.stream()
                .map(InventoryPojo::getBarcode)
                .collect(Collectors.toList());
       Map<String, ProductPojo> barcodeToProductMapping =  productService.getProductsBybarcodes(barcodes);
       List<InventoryReportData> inventoriesReportData = new ArrayList<>();

       for(InventoryPojo inventory : inventoryPojoList){
           InventoryReportData inventoryReportData = new InventoryReportData();
           inventoryReportData.setQuantity(inventory.getQuantity());
           ProductPojo product = barcodeToProductMapping.get(inventory.getBarcode());
           BrandPojo brand = brandService.getBrandById(product.getBrandId());
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


    private List<DailyData> getDailyData(
            Map<LocalDateTime, Integer> dateToOrdersQuantity,
            Map<LocalDateTime, Integer> dateToOrderItemsQuantity,
            Map<LocalDateTime, Integer> dateToRevenue){
        Iterator<Map.Entry<LocalDateTime, Integer>> itr = dateToOrdersQuantity.entrySet().iterator();
        List<DailyData> dailyDataList = new ArrayList<>();
        while(itr.hasNext())
        {
            Map.Entry<LocalDateTime, Integer> entry = itr.next();
            DailyData dailyData = new DailyData();
            LocalDateTime time = entry.getKey();
            int quantity = entry.getValue();
            dailyData.setOrdersQuantity(quantity);
            dailyData.setOrderItemsQuantity(dateToOrderItemsQuantity.get(time));
            dailyData.setRevenue(dateToRevenue.get(time));
            dailyDataList.add(dailyData);

        }
        return dailyDataList;
    }
    public List<DailyData> getDailyReport() throws ApiException {
        List<OrderPojo> orders = orderService.getAll();
        Map<LocalDateTime, Integer> dateToOrdersQuantity = new HashMap<>();
        Map<LocalDateTime, Integer> dateToOrderItemsQuantity = new HashMap<>();
        Map<LocalDateTime, Integer> dateToRevenue = new HashMap<>();

        for(OrderPojo order: orders){
            if(dateToOrdersQuantity.containsKey(order.getCreatedOn())) {
                dateToOrdersQuantity.put(order.getCreatedOn(), dateToOrdersQuantity.get(order.getCreatedOn()) + 1);
            }
            else{
                dateToOrdersQuantity.put(order.getCreatedOn(), 1);
            }
            List<OrderItemPojo> orderItems = orderItemService.getOrderItemByOrders(orders);
            if(dateToOrderItemsQuantity.containsKey(order.getCreatedOn())) {
                dateToOrderItemsQuantity.put(order.getCreatedOn(), dateToOrderItemsQuantity.get(order.getCreatedOn()) + orderItems.size());
            }
            else{
                dateToOrderItemsQuantity.put(order.getCreatedOn(), orderItems.size());
            }
            for(OrderItemPojo orderItem: orderItems){
                if(dateToRevenue.containsKey(order.getCreatedOn())) {
                    dateToRevenue.put(order.getCreatedOn(), dateToRevenue.get(order.getCreatedOn()) + orderItem.getQuantity()*orderItem.getPrice());
                }
                else{
                    dateToRevenue.put(order.getCreatedOn(), orderItem.getQuantity()*orderItem.getPrice());
                }
            }

        }
        return getDailyData(dateToOrdersQuantity,dateToOrderItemsQuantity,dateToRevenue);
    }

}
