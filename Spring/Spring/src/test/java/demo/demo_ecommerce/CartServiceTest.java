package demo.demo_ecommerce;

import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.repositories.*;
import demo.demo_ecommerce.services.CartService;
import demo.demo_ecommerce.services.CouponService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ShoppingCartItemRepository shoppingCartItemRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartByUserId_CreatesNewCartIfNotExist() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);
        Cart newCart = new Cart(user);

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.empty());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.save(any())).thenReturn(newCart);

        Cart result = cartService.getCartByUserId(userId);

        assertNotNull(result);
        verify(cartRepository).save(any());
    }

    @Test
    void testApplyCouponToCartItem_ValidCoupon() {
        Long itemId = 1L;
        String code = "SAVE10";

        ShoppingCartItem item = new ShoppingCartItem();
        item.setId(itemId);
        item.setPrice(BigDecimal.valueOf(100));

        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDiscountPercentage(BigDecimal.valueOf(10));

        when(shoppingCartItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(couponService.validateCouponForProduct(code, BigDecimal.valueOf(100))).thenReturn(true);
        when(couponRepository.findByCode(code)).thenReturn(Optional.of(coupon));

        boolean applied = cartService.applyCouponToCartItem(itemId, code);

        assertTrue(applied);
        assertEquals(BigDecimal.valueOf(90.00).setScale(2), item.getPrice());
        verify(shoppingCartItemRepository).save(item);
    }

    @Test
    void testAddItemToCart_NewItemAddedSuccessfully() {
        Long userId = 1L, productId = 2L;
        int quantity = 2;

        Product product = new Product();
        product.setId(productId);
        product.setStock(10);
        product.setPrice(BigDecimal.valueOf(50));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.empty());

        Cart result = cartService.addItemToCart(userId, productId, quantity);

        assertNotNull(result);
        verify(shoppingCartItemRepository).save(any());
    }

    @Test
    void testUpdateItemQuantity_RemoveItemIfZero() {
        Long userId = 1L, productId = 2L;

        Product product = new Product();
        product.setId(productId);
        product.setStock(5);

        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setQuantity(3);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(item)));

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.of(item));

        Cart updatedCart = cartService.updateItemQuantity(userId, productId, 0);

        assertNotNull(updatedCart);
        verify(shoppingCartItemRepository).delete(item);
    }

    @Test
    void testRemoveItemFromCart_RemovesItemSuccessfully() {
        Long userId = 1L, productId = 2L;

        Product product = new Product();
        product.setId(productId);

        ShoppingCartItem item = new ShoppingCartItem();
        item.setId(123L); // ✅ Imposta ID valido
        item.setProduct(product);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(item)));

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.of(item));

        Cart updated = cartService.removeItemFromCart(userId, productId);

        assertNotNull(updated);
        verify(shoppingCartItemRepository).deleteByIdWithoutVersion(123L); // ✅ Metodo corretto
    }


    @Test
    void testClearCart_EmptiesAllItems() {
        Long userId = 1L;

        ShoppingCartItem item1 = new ShoppingCartItem();
        item1.setId(100L);

        ShoppingCartItem item2 = new ShoppingCartItem();
        item2.setId(101L);

        List<ShoppingCartItem> items = List.of(item1, item2);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(items));

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

        Cart cleared = cartService.clearCart(userId);

        assertNotNull(cleared);
        verify(shoppingCartItemRepository).deleteByIdWithoutVersion(100L);
        verify(shoppingCartItemRepository).deleteByIdWithoutVersion(101L);

        // ❌ Non più necessario con l’implementazione attuale:
        // verify(shoppingCartItemRepository).findByCartId(cart.getId());
    }


    @Test
    void testAddItemToCart_ThrowsOnVersionConflict() {
        Long userId = 1L, productId = 2L;
        int quantity = 2;

        Product product = new Product();
        product.setId(productId);
        product.setStock(5);
        product.setPrice(BigDecimal.valueOf(50));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.empty());

        doThrow(new org.springframework.orm.ObjectOptimisticLockingFailureException(Product.class, productId))
                .when(shoppingCartItemRepository).save(any());

        assertThrows(org.springframework.orm.ObjectOptimisticLockingFailureException.class, () ->
                cartService.addItemToCart(userId, productId, quantity)
        );
    }


    @Test
    void testClearCart_OneItemAlreadyDeleted() {
        Long userId = 1L;

        ShoppingCartItem item1 = new ShoppingCartItem();
        item1.setId(100L);

        ShoppingCartItem item2 = new ShoppingCartItem();
        item2.setId(101L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(item1, item2)));

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

        doNothing().when(shoppingCartItemRepository).deleteByIdWithoutVersion(101L);
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1))
                .when(shoppingCartItemRepository).deleteByIdWithoutVersion(100L);

        Cart cleared = cartService.clearCart(userId);

        assertNotNull(cleared);
        verify(shoppingCartItemRepository).deleteByIdWithoutVersion(100L);
        verify(shoppingCartItemRepository).deleteByIdWithoutVersion(101L);
    }




}
