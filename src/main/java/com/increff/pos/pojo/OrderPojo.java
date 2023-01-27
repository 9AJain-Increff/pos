package com.increff.pos.pojo;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class OrderPojo {

    private LocalDateTime createdOn;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String orderURL;

}
