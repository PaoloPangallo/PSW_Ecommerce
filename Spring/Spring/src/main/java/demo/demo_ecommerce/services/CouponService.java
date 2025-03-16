package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Coupon;
import demo.demo_ecommerce.repositories.CouponRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;


    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code);
    }

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    // Validazione per l'intero ordine (o totale) usando un Double
    public boolean validateCoupon(String code, Double orderValue) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            return coupon.getIsActive() &&
                    coupon.getExpirationDate().isAfter(LocalDateTime.now()) &&
                    orderValue >= coupon.getMinOrderValue().doubleValue();
        }
        return false;
    }

    // Nuovo metodo per validare il coupon a livello di prodotto,
    // confrontando il prezzo del prodotto (o il totale dell'item) con il valore minimo richiesto
    public boolean validateCouponForProduct(String code, BigDecimal productPrice) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            boolean isActive = coupon.getIsActive();
            boolean notExpired = coupon.getExpirationDate().isAfter(LocalDateTime.now());
            boolean meetsMinValue = productPrice.compareTo(coupon.getMinOrderValue()) >= 0;

            LoggerFactory.getLogger(getClass()).info("Validating coupon {}: isActive={}, notExpired={}, productPrice={}, minOrderValue={} -> meetsMinValue={}",
                    code, isActive, notExpired, productPrice, coupon.getMinOrderValue(), meetsMinValue);

            return isActive && notExpired && meetsMinValue;
        }
        LoggerFactory.getLogger(getClass()).info("Coupon with code {} not found in validateCouponForProduct", code);
        return false;
    }

}
