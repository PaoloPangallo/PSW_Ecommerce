package demo.demo_ecommerce.repositories;


import demo.demo_ecommerce.entities.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    @Query("SELECT DISTINCT c FROM Coupon c LEFT JOIN FETCH c.products")
    List<Coupon> findAllWithProducts();


    @Query(value = "SELECT DISTINCT c FROM Coupon c LEFT JOIN FETCH c.products",
            countQuery = "SELECT COUNT(c) FROM Coupon c")
    Page<Coupon> findAllWithProductsPaged(Pageable pageable);

}
