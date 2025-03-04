package com.floristeria.blomst.dto.order;
import com.floristeria.blomst.enumeration.order.ProposalStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class OrderProposal {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private ProposalStatus status;
    private double offeredAmount;
    private double transportCost;
    private String rejectionReason;
    private LocalDateTime sentDate;
    private LocalDateTime rejectionDate;
    private LocalDateTime expirationDate;
}

