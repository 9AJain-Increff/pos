package com.increff.employee.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class DailyReportPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Integer ordersQuantity;
    private  Integer orderItemsQuantity;
    private Integer revenue;

    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

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
