package com.increff.pos.model;

// TODO: 29/01/23 remove the unused classes
public class DailyReportData {
    private Integer ordersCount;
    private Integer orderItemsCount;

    private Integer revenue;

    public Integer getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(Integer ordersCount) {
        this.ordersCount = ordersCount;
    }

    public Integer getOrderItemsCount() {
        return orderItemsCount;
    }

    public void setOrderItemsCount(Integer orderItemsCount) {
        this.orderItemsCount = orderItemsCount;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }
}
