package com.increff.pos.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class InventoryPojo {


    // FIXED: 29/01/23 there is no need of sep id
    @Id
    private Integer productId;
    private Integer quantity;
}
