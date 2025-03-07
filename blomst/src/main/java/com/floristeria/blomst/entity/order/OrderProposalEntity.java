package com.floristeria.blomst.entity.order;

import com.floristeria.blomst.entity.florista.FlowerShop;
import com.floristeria.blomst.entity.user.Auditable;
import com.floristeria.blomst.enumeration.order.ProposalStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_proposal")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OrderProposalEntity extends Auditable {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "flower_shop_id", nullable = false)
    private FlowerShop flowerShop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @Column(nullable = false)
    private double offeredAmount; 

    private double transportCost;

    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime sentDate; 

    private LocalDateTime acceptanceDate;
    private LocalDateTime rejectionDate;
    private LocalDateTime expirationDate;
}