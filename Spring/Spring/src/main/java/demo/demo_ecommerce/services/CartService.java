package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Coupon;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.CouponRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;

    public CartService(CartRepository cartRepository,
                       UsersRepository usersRepository,
                       ProductRepository productRepository,
                       ShoppingCartItemRepository shoppingCartItemRepository,
                       CouponRepository couponRepository,
                       CouponService couponService) {
        this.cartRepository = cartRepository;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.couponRepository = couponRepository;
        this.couponService = couponService;
    }

    /**
     * Recupera il carrello per l'utente con i relativi item e prodotti,
     * evitando problemi di LazyInitialization.
     */
    @Transactional
    public Cart getCartByUserId(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.getItems().size(); // Forza l'inizializzazione
            return cart;
        } else {
            Optional<Cart> cartBaseOpt = cartRepository.findByUserId(userId);
            if (cartBaseOpt.isPresent()) {
                Cart cart = cartBaseOpt.get();
                cart.getItems().size();
                return cart;
            } else {
                User user = usersRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                Cart newCart = new Cart(user);
                newCart = cartRepository.save(newCart);
                newCart.getItems().size();
                return newCart;
            }
        }
    }

    /**
     * Applica un coupon a un item del carrello, aggiornando il prezzo scontato.
     */
    @Transactional
    public boolean applyCouponToCartItem(Long cartItemId, String couponCode) {
        ShoppingCartItem item = shoppingCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + cartItemId));

        // Se lo stesso coupon è già applicato, esce
        if (item.getAppliedCoupon() != null && item.getAppliedCoupon().getCode().equals(couponCode)) {
            return false;
        }

        // Valida il coupon in base al prezzo corrente dell'item
        boolean isValid = couponService.validateCouponForProduct(couponCode, item.getPrice());
        if (!isValid) {
            return false;
        }

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found with code: " + couponCode));

        // Memorizza il prezzo attuale come oldPrice (se non già impostato)
        if (item.getOldPrice() == null) {
            item.setOldPrice(item.getPrice());
        }

        // Calcola il nuovo prezzo scontato in base alla percentuale di sconto
        BigDecimal discountRate = coupon.getDiscountPercentage().divide(BigDecimal.valueOf(100));
        BigDecimal newPrice = item.getPrice().subtract(item.getPrice().multiply(discountRate));
        newPrice = newPrice.setScale(2, RoundingMode.HALF_UP);

        // Aggiorna il prezzo e applica il coupon
        item.setPrice(newPrice);
        item.setAppliedCoupon(coupon);

        shoppingCartItemRepository.save(item);
        return true;
    }


    public static class CartSummary {
        private final int itemCount;
        private final BigDecimal total;

        public CartSummary(int itemCount, BigDecimal total) {
            this.itemCount = itemCount;
            this.total = total;
        }

        public int getItemCount() {
            return itemCount;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }

    @Transactional
    public CartSummary getCartSummary(Long userId) {
        Cart cart = getCartByUserId(userId);
        int itemCount = cart.getItems().stream()
                .mapToInt(ShoppingCartItem::getQuantity)
                .sum();
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return new CartSummary(itemCount, total);
    }





    /**
     * Aggiunge un item al carrello, controllando lo stock e inizializzando i prezzi.
     */
    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        if (userId == null) {
            throw new IllegalStateException("Devi essere loggato per aggiungere prodotti al carrello.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero.");
        }

        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato con ID: " + productId));
        int availableStock = product.getStock();

        Optional<ShoppingCartItem> existingItemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingItemOpt.isPresent()) {
            ShoppingCartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;

            if (newQuantity > availableStock) {
                throw new IllegalArgumentException("La quantità richiesta supera lo stock disponibile. Stock massimo: " + availableStock);
            }

            existingItem.setQuantity(newQuantity);
            shoppingCartItemRepository.save(existingItem); // trigger @Version
        } else {
            if (quantity > availableStock) {
                throw new IllegalArgumentException("La quantità richiesta supera lo stock disponibile. Stock massimo: " + availableStock);
            }

            ShoppingCartItem newItem = new ShoppingCartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);

            BigDecimal basePrice = product.getPrice();
            Integer discountPercentage = product.getDiscountPercentage();

            if (discountPercentage != null && discountPercentage > 0) {
                BigDecimal discountAmount = basePrice
                        .multiply(BigDecimal.valueOf(discountPercentage))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal discountedPrice = basePrice.subtract(discountAmount);
                newItem.setPrice(discountedPrice);
                newItem.setOldPrice(basePrice);
            } else {
                newItem.setPrice(basePrice);
                newItem.setOldPrice(null);
            }

            shoppingCartItemRepository.save(newItem); // trigger @Version
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart); // trigger @Version su Cart
        return getCartByUserId(userId); // ricarica sicura e aggiornata
    }



    /**
     * Aggiorna la quantità di un item nel carrello.
     */
    @Transactional
    public Cart updateItemQuantity(Long userId, Long productId, int newQuantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        int availableStock = product.getStock();

        Optional<ShoppingCartItem> itemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (itemOpt.isPresent()) {
            ShoppingCartItem item = itemOpt.get();

            if (newQuantity <= 0) {
                shoppingCartItemRepository.delete(item);
                cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
            } else {
                if (newQuantity > availableStock) {
                    throw new IllegalArgumentException("Quantità superiore allo stock disponibile: " + availableStock);
                }
                item.setQuantity(newQuantity);
                shoppingCartItemRepository.save(item);  // trigger su @Version
            }
        } else {
            if (newQuantity > 0) {
                if (newQuantity > availableStock) {
                    throw new IllegalArgumentException("Quantità superiore allo stock disponibile: " + availableStock);
                }

                ShoppingCartItem newItem = ShoppingCartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(newQuantity)
                        .price(product.getPrice())
                        .oldPrice(product.getPrice())
                        .build();

                shoppingCartItemRepository.save(newItem);
                cart.getItems().add(newItem);
            } else {
                throw new IllegalArgumentException("Quantità non valida.");
            }
        }

        cartRepository.save(cart); // trigger su @Version Cart
        return getCartByUserId(userId);
    }

    /**
     * Rimuove un item dal carrello.
     */
    @Transactional
    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);

        shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresent(item -> {
                    shoppingCartItemRepository.deleteByIdWithoutVersion(item.getId());
                    // Hibernate tiene la collezione sincronizzata in memoria
                    cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
                });

        // ❌ NIENTE cartRepository.save(cart);
        return cart;
    }




    /**
     * Svuota il carrello dell'utente.
     */
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);

        for (ShoppingCartItem item : List.copyOf(cart.getItems())) {
            try {
                shoppingCartItemRepository.deleteByIdWithoutVersion(item.getId());
            } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
                // Logga o ignora: item già rimosso
                logger.warn("Elemento già cancellato dal carrello: ID={}", item.getId());
            }
            cart.getItems().remove(item); // mantiene Hibernate sincronizzato
        }

        return cart;
    }






}
