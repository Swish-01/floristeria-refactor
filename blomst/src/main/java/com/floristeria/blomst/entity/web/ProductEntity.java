package com.floristeria.blomst.entity.web;

import java.util.List;

import com.floristeria.blomst.entity.order.OrderEntity;
import com.floristeria.blomst.entity.user.Auditable;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProductEntity extends Auditable {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "LONGTEXT", nullable = true)
    private String shortDescription;

    private String type;

    private String status;

    private boolean featured;

    private String sku;

    private String regularPrice;

    private String salePrice;

    private boolean isVirtual;

    private boolean soldIndividually;

    private String weight;

    private boolean shippingRequired;

    private boolean shippingTaxable;

    private String purchaseNote;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductVariationEntity> variations;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToMany(mappedBy = "products")
    private List<OrderEntity> orders;
}
