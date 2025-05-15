package demo.demo_ecommerce;// File: CartServiceTest.java

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Coupon;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.CouponRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import demo.demo_ecommerce.services.CartService;
import demo.demo_ecommerce.services.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private ShoppingCartItemRepository shoppingCartItemRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void applyCouponToCartItem_ShouldApplyCoupon() {
        ShoppingCartItem item = new ShoppingCartItem();
        item.setPrice(BigDecimal.valueOf(100));

        Coupon coupon = new Coupon();
        coupon.setCode("SAVE10");
        coupon.setDiscountPercentage(BigDecimal.valueOf(10));

        when(shoppingCartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(couponService.validateCouponForProduct("SAVE10", BigDecimal.valueOf(100))).thenReturn(true);
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));

        boolean result = cartService.applyCouponToCartItem(1L, "SAVE10");

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(90.00).setScale(2), item.getPrice());
        assertEquals(coupon, item.getAppliedCoupon());
        verify(shoppingCartItemRepository, times(1)).save(item);
    }

    @Test
    void applyCouponToCartItem_ShouldFailIfCouponInvalid() {
        ShoppingCartItem item = new ShoppingCartItem();
        item.setPrice(BigDecimal.valueOf(100));

        when(shoppingCartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(couponService.validateCouponForProduct("INVALID", BigDecimal.valueOf(100))).thenReturn(false);

        boolean result = cartService.applyCouponToCartItem(1L, "INVALID");

        assertFalse(result);
        verify(shoppingCartItemRepository, never()).save(any());
    }
}
