package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CouponCreationDTO;
import demo.demo_ecommerce.dtos.CouponResponseDTO;
import demo.demo_ecommerce.entities.Coupon;
import demo.demo_ecommerce.repositories.CouponRepository;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.services.CouponService;
import demo.demo_ecommerce.entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Nuovo endpoint per ottenere tutti i coupon come DTO
    @GetMapping
    public List<CouponResponseDTO> getAllCoupons() {
        return couponService.getAllCoupons();
    }


    @GetMapping("/{code}")
    public Optional<Coupon> getCouponByCode(@PathVariable String code) {
        return couponService.findByCode(code);
    }

    // Utilizziamo solo il metodo che accetta CouponCreationDTO
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon createCoupon(@RequestBody CouponCreationDTO couponDto) {
        return couponService.createCouponFromDTO(couponDto);
    }

    @GetMapping("/validate/{code}/{orderValue}")
    public boolean validateCoupon(@PathVariable String code, @PathVariable Double orderValue) {
        return couponService.validateCoupon(code, orderValue);
    }

    @GetMapping("/applicable/{productId}")
    public List<Coupon> getApplicableCoupons(@PathVariable Long productId) {
        // Carica tutti i coupon + products in un'unica query
        List<Coupon> allCoupons = couponRepository.findAllWithProducts();

        return allCoupons.stream()
                .filter(coupon -> coupon.getIsActive()
                        && coupon.getExpirationDate().isAfter(LocalDateTime.now())
                        && coupon.getProducts().stream().anyMatch(p -> p.getId().equals(productId)))
                .collect(Collectors.toList());
    }

    @GetMapping("/used-by-user/{userId}")
    public List<Coupon> getUserUsedCoupons(@PathVariable Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(Order::getCoupon)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
