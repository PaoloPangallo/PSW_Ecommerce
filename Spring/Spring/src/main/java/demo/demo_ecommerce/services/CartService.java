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
            shoppingCartItemRepository.save(existingItem);
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

            shoppingCartItemRepository.save(newItem);
        }

        cartRepository.save(cart);
        return getCartByUserId(userId);
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

        logger.info(">> Carrello prima dell'update: {} con {} item.", cart.getId(), cart.getItems().size());

        Optional<ShoppingCartItem> itemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (itemOpt.isPresent()) {
            ShoppingCartItem item = itemOpt.get();
            logger.info(">> Item trovato per productId {}. Quantità attuale: {}", productId, item.getQuantity());

            if (newQuantity <= 0) {
                shoppingCartItemRepository.delete(item);
                cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
                logger.info(">> Item rimosso per productId {}", productId);
            } else {
                if (newQuantity > availableStock) {
                    throw new IllegalArgumentException("La quantità richiesta supera lo stock disponibile. Stock massimo: " + availableStock);
                }
                item.setQuantity(newQuantity);
                shoppingCartItemRepository.save(item);
                logger.info(">> Quantità aggiornata per productId {} a {}", productId, newQuantity);
            }
        } else {
            if (newQuantity > 0) {
                if (newQuantity > availableStock) {
                    throw new IllegalArgumentException("La quantità richiesta supera lo stock disponibile. Stock massimo: " + availableStock);
                }
                ShoppingCartItem newItem = new ShoppingCartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(newQuantity);
                // Inizializza i prezzi al valore di listino del prodotto
                newItem.setPrice(product.getPrice());
                newItem.setOldPrice(product.getPrice());
                shoppingCartItemRepository.save(newItem);
                cart.getItems().add(newItem);
                logger.info(">> Nuovo item aggiunto per productId {} con quantità {}", productId, newQuantity);
            } else {
                throw new IllegalArgumentException("Impossibile aggiornare un item con quantità non positiva.");
            }
        }

        cartRepository.save(cart);
        Cart updatedCart = getCartByUserId(userId);
        logger.info(">> Carrello aggiornato finale: {} item.", updatedCart.getItems().size());
        updatedCart.getItems().forEach(i ->
                logger.info("   - productId: {}, quantity: {}", i.getProduct().getId(), i.getQuantity())
        );
        return updatedCart;
    }

    /**
     * Rimuove un item dal carrello.
     */
    @Transactional
    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        ShoppingCartItem itemToRemove = shoppingCartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato nel carrello."));

        shoppingCartItemRepository.delete(itemToRemove);
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));

        cartRepository.save(cart);
        logger.info("Dopo removeItem, nel DB il carrello ha {} item:", cart.getItems().size());
        cart.getItems().forEach(i ->
                logger.info("  - productId: {}, quantity={}", i.getProduct().getId(), i.getQuantity())
        );
        return getCartByUserId(userId);
    }

    /**
     * Svuota il carrello dell'utente.
     */
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        shoppingCartItemRepository.deleteAllByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
        return getCartByUserId(userId);
    }
}
