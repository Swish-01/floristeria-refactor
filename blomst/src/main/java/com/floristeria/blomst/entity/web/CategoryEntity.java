package com.floristeria.blomst.entity.web;

import java.util.List;

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
@Table(name = "category")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CategoryEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false, unique = true)
    private Long wooCommerceId;
    
    @ManyToOne
    @JoinColumn(name = "web_id", nullable = false)
    private WebEntity web;


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;
}
