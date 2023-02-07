package com.increff.pos.pojo;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"orderURL"})}
)
public class OrderPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime createdOn;
    private String orderURL;

}
