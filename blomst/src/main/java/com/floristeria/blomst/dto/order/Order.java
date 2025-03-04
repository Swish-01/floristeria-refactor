package com.floristeria.blomst.dto.order;

import java.util.List;

import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Order {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private String orderNumber;
    private String status;
    private List<OrderProposal> proposals;

    public Order(Long id, Long createdBy, Long updatedBy, String orderNumber, String status) {
        this.id = id;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.orderNumber = orderNumber;
        this.status = status;
        this.proposals = Collections.emptyList();
    }
}
