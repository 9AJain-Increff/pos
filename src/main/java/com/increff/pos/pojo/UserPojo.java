package com.increff.pos.pojo;

import com.increff.pos.model.auth.UserRole;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email" })},
        indexes = {@Index(columnList = "email")}
)
public class UserPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password;
    private UserRole role;

}
