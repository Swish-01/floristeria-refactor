package com.floristeria.blomst.entity.customer;

import com.floristeria.blomst.entity.order.OrderEntity;
import com.floristeria.blomst.entity.user.Auditable;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CustomerEntity extends Auditable {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String postcode;
    private String country;
    @Column(unique = true, nullable = false)
    private String email;
    private String phone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderEntity> orders;

}
