package com.floristeria.blomst.entity.web;



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
@Table(name = "product_variation")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProductVariationEntity extends Auditable {

    private String sku;

    private String regularPrice;

    private String salePrice; 

    private boolean onSale;

    private String status;

    private boolean isVirtual;

    private String weight;
    private String dimensions;
    private String shippingClass;
    private String imageSrc;
    private String imageAlt;

    private String metaData;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
