package com.floristeria.blomst.entity.florista;

import java.util.Set;

import com.floristeria.blomst.entity.order.OrderEntity;
import com.floristeria.blomst.entity.user.Auditable;
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
@Table(name = "flower_shop")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FlowerShop extends Auditable {
    private String name;
    private String companyName;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String imageUrl;
    private String cif;
    private String accountNumber;
    private String observations;
    private String contactPerson;
    @OneToMany(mappedBy = "flowerShop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderEntity> orders;
}
