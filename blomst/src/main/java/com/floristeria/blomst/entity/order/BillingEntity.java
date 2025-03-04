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
@Table(name = "billing")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BillingEntity extends Auditable{
    private String first_name;
    private String last_name;
    private String company;
    private String address_1;
    private String address_2;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String email;
    private String phone;
}
