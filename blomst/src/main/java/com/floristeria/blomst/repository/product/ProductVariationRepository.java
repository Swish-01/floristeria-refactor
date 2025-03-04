package com.floristeria.blomst.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.floristeria.blomst.entity.web.ProductVariationEntity;

@Repository
public interface ProductVariationRepository  extends JpaRepository<ProductVariationEntity,Long>{
    
}
