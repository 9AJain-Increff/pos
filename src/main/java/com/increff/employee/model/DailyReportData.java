package com.increff.employee.model;

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
