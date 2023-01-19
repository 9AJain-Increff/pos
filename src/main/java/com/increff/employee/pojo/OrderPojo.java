package com.increff.employee.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class OrderPojo {

    private LocalDateTime createdOn;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String orderURL;

}
