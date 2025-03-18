package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.CouponCreationDTO;
import demo.demo_ecommerce.dtos.CouponResponseDTO;
import demo.demo_ecommerce.entities.Coupon;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.CouponRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;

    public CouponService(CouponRepository couponRepository, ProductRepository productRepository) {
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
    }


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

    public Coupon createCouponFromDTO(CouponCreationDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setCode(dto.getCode());
        coupon.setDiscountPercentage(dto.getDiscountPercentage());
        coupon.setExpirationDate(dto.getExpirationDate());
        coupon.setIsActive(dto.getIsActive());
        coupon.setMinOrderValue(dto.getMinOrderValue());

        // Carica i prodotti associati
        List<Product> products = productRepository.findAllById(dto.getProductIds());
        coupon.setProducts(products);

        return couponRepository.save(coupon);
    }
    public List<CouponResponseDTO> getAllCoupons() {
        // Carichiamo i coupon con i prodotti associati
        List<Coupon> coupons = couponRepository.findAllWithProducts();

        return coupons.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Esempio di mappatura
    private CouponResponseDTO toResponseDTO(Coupon coupon) {
        CouponResponseDTO dto = new CouponResponseDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDiscountPercentage(coupon.getDiscountPercentage());
        dto.setExpirationDate(coupon.getExpirationDate());
        dto.setIsActive(coupon.getIsActive());
        dto.setMinOrderValue(coupon.getMinOrderValue());

        // Mappiamo i prodotti associati (in questo esempio, solo i nomi)
        List<String> productNames = coupon.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toList());
        dto.setProductNames(productNames);

        return dto;
    }

}
