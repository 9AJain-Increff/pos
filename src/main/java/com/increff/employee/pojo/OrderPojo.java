package com.increff.employee.pojo;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class OrderPojo {

    private LocalDateTime createdOn;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;


    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
