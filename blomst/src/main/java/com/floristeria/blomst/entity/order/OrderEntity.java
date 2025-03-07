package com.floristeria.blomst.entity.order;

import java.time.LocalDateTime;
import java.util.Set;

import com.floristeria.blomst.entity.customer.CustomerEntity;
import com.floristeria.blomst.entity.florista.FlowerShop;
import com.floristeria.blomst.entity.user.Auditable;
import com.floristeria.blomst.entity.user.UserEntity;
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

    private String discount_total;
    private String discount_tax;
    private String shipping_total;
    private String shipping_tax;
    private String cart_tax;
    private String total;
    private String total_tax;
    private String order_key;
    private String payment_method;
    private String payment_method_title;
    private String transaction_id;
    private String customer_ip_address;
    private String customer_user_agent;
    private String created_via;
    private String customer_note;
    private LocalDateTime date_completed;
    private LocalDateTime date_paid;
    private String cart_hash;
    private String payment_url;

    private LocalDateTime ceremonyDate;
    private String additional_information;
    @Column(length = 500)
    private String dedicatory;
    @Column(length = 500)
    private String delivery_card;

    private String auth;

    // web relacionada
    @ManyToOne
    @JoinColumn(name = "web_id", nullable = false)
    private WebEntity web;

    // productos del pedido
    @ManyToMany
    @JoinTable(name = "order_product", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<ProductEntity> products;

    // florista asociado
    @ManyToOne
    @JoinColumn(name = "flower_shop_id")
    private FlowerShop flowerShop;

    // datos de las propuestas de pedido
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderProposalEntity> proposals;

    // datos de facturacion del cliente
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_id", referencedColumnName = "id")
    private BillingEntity billing;

    // datos de envio del pedido
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_id", referencedColumnName = "id")
    private ShippingEntity shipping;


    // aunque la clase herede de auditable, voy a poner una relacion con el usuario para las comisiones. 
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity assignedUser;

    // relacion con el cliente que hace el pedido
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

}
