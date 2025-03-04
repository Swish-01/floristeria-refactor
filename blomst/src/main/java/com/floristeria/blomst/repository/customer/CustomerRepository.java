package com.floristeria.blomst.repository.customer;

import com.floristeria.blomst.dto.customer.Customer;
import com.floristeria.blomst.entity.customer.CustomerEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    @Query("SELECT COUNT(c) FROM CustomerEntity c WHERE c.email = :email")
    long countByEmail(String email);

    @Query("SELECT new com.floristeria.blomst.dto.customer.Customer(" +
            "c.id, c.referenceId, c.firstName, c.lastName, c.address, c.city, c.state, c.postcode, " +
            "c.country, c.email, c.phone) " +
            "FROM CustomerEntity c WHERE c.email = :email")
    Optional<Customer> findCustomerDTOByEmail(String email);

    @Query("SELECT new com.floristeria.blomst.dto.customer.Customer(" +
            "c.id, c.referenceId, c.firstName, c.lastName, c.address, c.city, c.state, c.postcode, " +
            "c.country, c.email, c.phone) " +
            "FROM CustomerEntity c WHERE c.id = :id")
    Optional<Customer> findCustomerDTOById(Long id);


    @Query("SELECT c FROM CustomerEntity c WHERE c.email = :email")
    Optional<CustomerEntity> findByEmail(String email);

    @Query("SELECT new com.floristeria.blomst.dto.customer.Customer(" +
            "c.id, c.referenceId, c.firstName, c.lastName, c.address, c.city, c.state, c.postcode, " +
            "c.country, c.email, c.phone) " +
            "FROM CustomerEntity c " +
            "WHERE (:firstName IS NULL OR c.firstName LIKE %:firstName%) AND " +
            "(:lastName IS NULL OR c.lastName LIKE %:lastName%) AND " +
            "(:address IS NULL OR c.address LIKE %:address%) AND " +
            "(:city IS NULL OR c.city LIKE %:city%) AND " +
            "(:state IS NULL OR c.state LIKE %:state%) AND " +
            "(:postcode IS NULL OR c.postcode LIKE %:postcode%) AND " +
            "(:country IS NULL OR c.country LIKE %:country%) AND " +
            "(:email IS NULL OR c.email LIKE %:email%) AND " +
            "(:phone IS NULL OR c.phone LIKE %:phone%) order by c.id desc")
    Page<Customer> findCustomersByFilters(String firstName, String lastName, String address, String city, String state,
                                          String postcode, String country, String email, String phone, Pageable pageable);

    @Query("SELECT new com.floristeria.blomst.dto.customer.Customer(" +
            "c.id, c.firstName, c.lastName, c.email, c.phone) " +
            "FROM CustomerEntity c " +
            "WHERE (:firstName IS NULL OR c.firstName LIKE %:firstName%) AND " +
            "(:lastName IS NULL OR c.lastName LIKE %:lastName%) AND " +
            "(:email IS NULL OR c.email LIKE %:email%) AND " +
            "(:phone IS NULL OR c.phone LIKE %:phone%) order by c.id desc")
    Page<Customer> findCustomersByFilters(String firstName, String lastName, String email, String phone, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE CustomerEntity c SET c.referenceId = :referenceId, c.firstName = :firstName, " +
            "c.lastName = :lastName, c.address = :address, c.city = :city, c.state = :state, " +
            "c.postcode = :postcode, c.country = :country, c.email = :email, c.phone = :phone " +
            "WHERE c.id = :id")
    int updateCustomerById(@Param("id") Long id,
                           @Param("referenceId") String referenceId,
                           @Param("firstName") String firstName,
                           @Param("lastName") String lastName,
                           @Param("address") String address,
                           @Param("city") String city,
                           @Param("state") String state,
                           @Param("postcode") String postcode,
                           @Param("country") String country,
                           @Param("email") String email,
                           @Param("phone") String phone);


}
