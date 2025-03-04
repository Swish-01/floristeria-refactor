package com.floristeria.blomst.entity.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.floristeria.blomst.entity.user.Auditable;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipping")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ShippingEntity extends Auditable{
    private String first_name;
    private String last_name;
    private String company;
    private String address_1;
    private String address_2;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String phone;
}
