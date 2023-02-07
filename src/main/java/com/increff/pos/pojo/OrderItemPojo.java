package com.increff.pos.pojo;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        indexes = {@Index(columnList = "orderId")}
)
public class OrderItemPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Integer orderId;
    private Float sellingPrice;

}
