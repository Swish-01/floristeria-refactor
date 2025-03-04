package com.floristeria.blomst.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Customer {
    private Long id;
//    private Long createdBy;
//    private Long updatedBy;
    private String referenceId;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String email;
    private String phone;

    public Customer(Long id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
    //    private String createdAt;
//    private String updatedAt;

}
