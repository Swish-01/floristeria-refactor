package com.floristeria.blomst.entity.order;

import java.time.LocalDateTime;
import java.util.Set;

import com.floristeria.blomst.entity.florista.FlowerShop;
import com.floristeria.blomst.entity.user.Auditable;
import com.floristeria.blomst.entity.web.ProductEntity;
import com.floristeria.blomst.entity.web.WebEntity;
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
@Table(name = "`order`")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OrderEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String status;

    private Double total;

    private String payment_method;
    private String payment_method_title;

    private String created_via;

    private String customer_note;
    private String payment_url;

    private LocalDateTime ceremonyDate;
    @Column(length = 500)
    private String dedicatory;
    @Column(length = 500)
    private String delivery_card;

    private String auth;

    @ManyToOne
    @JoinColumn(name = "web_id", nullable = false)
    private WebEntity web;

    @ManyToMany
    @JoinTable(name = "order_product", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<ProductEntity> products;

    @ManyToOne
    @JoinColumn(name = "flower_shop_id")
    private FlowerShop flowerShop;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderProposalEntity> proposals;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_id", referencedColumnName = "id")
    private BillingEntity billing;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_id", referencedColumnName = "id")
    private ShippingEntity shipping;

}
