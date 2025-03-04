package com.floristeria.blomst.repository.web;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.floristeria.blomst.dto.web.Web;
import com.floristeria.blomst.entity.web.WebEntity;

import jakarta.transaction.Transactional;

@Repository
public interface WebRepository extends JpaRepository<WebEntity, Long> {

    @Query("SELECT new com.floristeria.blomst.dto.web.Web(w.id, w.createdBy, w.updatedBy, w.url, w.name, w.urlLogo) FROM WebEntity w WHERE w.id = :id")
    Optional<Web> findWebById(Long id);

    @Query("SELECT new com.floristeria.blomst.dto.web.Web(w.id, w.createdBy, w.updatedBy, w.url, w.name, w.urlLogo) FROM WebEntity w")
    Page<Web> findWebs(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE WebEntity w SET w.customerKey = :customerKey, w.secretkey = :secretKey WHERE w.id = :webId")
    int updateWebKeys(Long webId,String customerKey, String secretKey);
    
    @Modifying
    @Transactional
    @Query("UPDATE WebEntity w SET w.urlLogo = :urlLogo WHERE w.id = :webId")
    int updateWebLogo(Long webId, String urlLogo);
    
}
