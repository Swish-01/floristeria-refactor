package com.floristeria.blomst.entity.customer;

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
}
