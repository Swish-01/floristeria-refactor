package com.floristeria.blomst.entity.web;
import java.util.List;

import com.floristeria.blomst.entity.order.OrderEntity;
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
@Table(name = "web")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WebEntity extends Auditable {

    @Column(nullable = false, unique = true)
    private String url;
    
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String urlLogo;
    
    @Column(nullable = false)
    private String customerKey;
    
    @Column(nullable = false)
    private String secretkey;

    
    @OneToMany(mappedBy = "web", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CategoryEntity> categories;
    
    @OneToMany(mappedBy = "web", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderEntity> orders;
}
