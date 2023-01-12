package com.increff.employee.model;

import org.hibernate.secure.spi.IntegrationException;

public class DailyData {
    private Integer ordersQuantity;
    private  Integer orderItemsQuantity;
    private Integer revenue;

    public Integer getOrdersQuantity() {
        return ordersQuantity;
    }

    public void setOrdersQuantity(Integer ordersQuantity) {
        this.ordersQuantity = ordersQuantity;
    }

    public Integer getOrderItemsQuantity() {
        return orderItemsQuantity;
    }

    public void setOrderItemsQuantity(Integer orderItemsQuantity) {
        this.orderItemsQuantity = orderItemsQuantity;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }
}
