package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.CouponCreationDTO;
import demo.demo_ecommerce.dtos.CouponResponseDTO;
import demo.demo_ecommerce.entities.Coupon;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.CouponRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;

    public CouponService(CouponRepository couponRepository, ProductRepository productRepository, UsersRepository usersRepository) {
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
    }

    @Cacheable(value = "couponsPaged", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize")
    public Page<CouponResponseDTO> getAllCouponsPaged(Pageable pageable) {
        Page<Coupon> page = couponRepository.findAllWithProductsPaged(pageable);
        List<CouponResponseDTO> dtoList = page.getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    @Cacheable(value = "couponByCode", key = "#code", unless = "#result == null or #result.isEmpty()")
    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code);
    }

    @Caching(evict = {
            @CacheEvict(value = "coupons", allEntries = true),
            @CacheEvict(value = "couponsPaged", allEntries = true),
            @CacheEvict(value = "couponByCode", allEntries = true),
            @CacheEvict(value = "couponValidation", allEntries = true),
            @CacheEvict(value = "couponValidationProduct", allEntries = true)
    })
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Cacheable(value = "couponValidation", key = "#code + '-' + #orderValue")
    public boolean validateCoupon(String code, Double orderValue) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        return couponOpt.map(coupon -> coupon.getIsActive()
                && coupon.getExpirationDate().isAfter(LocalDateTime.now())
                && orderValue >= coupon.getMinOrderValue().doubleValue()
        ).orElse(false);
    }

    @Cacheable(value = "couponValidationProduct", key = "#code + '-' + #productPrice")
    public boolean validateCouponForProduct(String code, BigDecimal productPrice) {
        if (productPrice == null) {
            logger.warn("❌ validateCouponForProduct: prezzo nullo, impossibile validare coupon '{}'", code);
            return false;
        }

        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        return couponOpt.map(coupon -> {
            boolean isValid = coupon.getIsActive()
                    && coupon.getExpirationDate().isAfter(LocalDateTime.now())
                    && productPrice.compareTo(coupon.getMinOrderValue()) >= 0;

            logger.info("Validating coupon {}: valid={}", code, isValid);
            return isValid;
        }).orElse(false);
    }


    @Caching(evict = {
            @CacheEvict(value = "coupons", allEntries = true),
            @CacheEvict(value = "couponsPaged", allEntries = true),
            @CacheEvict(value = "couponByCode", key = "#dto.code"),
            @CacheEvict(value = "couponValidation", allEntries = true),
            @CacheEvict(value = "couponValidationProduct", allEntries = true)
    })
    public Coupon createCouponFromDTO(CouponCreationDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setCode(dto.getCode());
        coupon.setDiscountPercentage(dto.getDiscountPercentage());
        coupon.setExpirationDate(dto.getExpirationDate());
        coupon.setIsActive(dto.getIsActive());
        coupon.setMinOrderValue(dto.getMinOrderValue());

        List<Product> products = productRepository.findAllById(dto.getProductIds());
        coupon.setProducts(products);

        return couponRepository.save(coupon);
    }

    @Cacheable(value = "coupons", unless = "#result == null or #result.isEmpty()")
    public List<CouponResponseDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAllWithProducts();
        return coupons.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public boolean isUserRequestingOwnData(Long userId, String email) {
        return usersRepository.findById(userId)
                .map(u -> u.getEmail().equalsIgnoreCase(email))
                .orElse(false);
    }

    private CouponResponseDTO toResponseDTO(Coupon coupon) {
        CouponResponseDTO dto = new CouponResponseDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDiscountPercentage(coupon.getDiscountPercentage());
        dto.setExpirationDate(coupon.getExpirationDate());
        dto.setIsActive(coupon.getIsActive());
        dto.setMinOrderValue(coupon.getMinOrderValue());

        List<String> productNames = coupon.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toList());
        dto.setProductNames(productNames);

        return dto;
    }
}
