package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final CouponRepository couponRepository;

   private final CouponService couponService;
// Aggiunto repository

    public CartService(CartRepository cartRepository,
                       UsersRepository usersRepository,
                       ProductRepository productRepository,
                       ShoppingCartItemRepository shoppingCartItemRepository, CouponRepository couponRepository, CouponService couponService) { // Aggiunto repository nel costruttore
        this.cartRepository = cartRepository;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.couponRepository = couponRepository;
        this.couponService = couponService;
    }

    @Transactional
    public Cart getCartByUserId(Long userId) {
        // Prova a caricare il carrello con tutti gli item e i relativi prodotti
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            // Forza il caricamento degli items
            cart.getItems().size();
            return cart;
        } else {
            // Se non troviamo il carrello con join fetch, proviamo a caricare il carrello "base"
            Optional<Cart> cartBaseOpt = cartRepository.findByUserId(userId);
            if (cartBaseOpt.isPresent()) {
                return cartBaseOpt.get();
            } else {
                // Se non esiste un carrello, creiamo un nuovo carrello vuoto per l'utente
                User user = usersRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                Cart newCart = new Cart(user);
                newCart = cartRepository.save(newCart);
                // Forza il caricamento degli items (sarà vuoto, ma per coerenza)
                newCart.getItems().size();
                return newCart;
            }
        }
    }


    @Transactional
    public boolean applyCouponToCartItem(Long cartItemId, String couponCode) {
        ShoppingCartItem item = shoppingCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + cartItemId));

        // Se lo stesso coupon è già applicato, non fare nulla
        if (item.getAppliedCoupon() != null && item.getAppliedCoupon().getCode().equals(couponCode)) {
            return false;
        }

        BigDecimal productPrice = item.getProduct().getPrice();
        boolean isValid = couponService.validateCouponForProduct(couponCode, productPrice);
        if (!isValid) {
            return false;
        }

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found with code: " + couponCode));

        // Applica il coupon all'item del carrello
        item.setAppliedCoupon(coupon);
        shoppingCartItemRepository.save(item);

        // Aggiorna anche la preferenza nel carrello
        Cart cart = item.getCart();
        cart.getSelectedCoupons().put(item.getProduct().getId(), couponCode);
        cartRepository.save(cart);

        return true;
    }




    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero.");
        }

        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato con ID: " + productId));

        int availableStock = product.getStock(); // Assumiamo che esista il metodo getStock() nell'entità Product

        // Controlla se il prodotto è già presente nel carrello
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
            shoppingCartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }
    @Transactional
    public Cart updateItemQuantity(Long userId, Long productId, int newQuantity) {
        // Recuperiamo il carrello
        Cart cart = getCartByUserId(userId);
        // Recuperiamo il prodotto per conoscere lo stock
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        int availableStock = product.getStock();

        System.out.println(">> Carrello prima dell'update: " + cart.getId() + " con " + cart.getItems().size() + " item.");

        Optional<ShoppingCartItem> itemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (itemOpt.isPresent()) {
            ShoppingCartItem item = itemOpt.get();
            System.out.println(">> Item trovato per productId " + productId + ". Quantità attuale: " + item.getQuantity());

            if (newQuantity <= 0) {
                // Rimuoviamo l'item se la quantità è zero o negativa
                shoppingCartItemRepository.delete(item);
                cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
                System.out.println(">> Item rimosso per productId " + productId);
            } else {
                // Controlliamo che newQuantity non superi lo stock
                if (newQuantity > availableStock) {
                    throw new IllegalArgumentException("La quantità richiesta supera lo stock disponibile. Stock massimo: " + availableStock);
                }
                // Aggiorniamo la quantità
                item.setQuantity(newQuantity);
                shoppingCartItemRepository.save(item);
                System.out.println(">> Quantità aggiornata per productId " + productId + " a " + newQuantity);
            }
        } else {
            // Se l'item non esiste e la quantità è positiva, aggiungilo (controllando lo stock)
            if (newQuantity > 0) {
                if (newQuantity > availableStock) {
                    throw new IllegalArgumentException("La quantità richiesta supera lo stock disponibile. Stock massimo: " + availableStock);
                }
                ShoppingCartItem newItem = new ShoppingCartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(newQuantity);
                shoppingCartItemRepository.save(newItem);
                cart.getItems().add(newItem);
                System.out.println(">> Nuovo item aggiunto per productId " + productId + " con quantità " + newQuantity);
            } else {
                throw new IllegalArgumentException("Impossibile aggiornare un item con quantità non positiva.");
            }
        }

        cartRepository.save(cart);
        Cart updatedCart = getCartByUserId(userId);
        System.out.println(">> Carrello aggiornato finale: " + updatedCart.getItems().size() + " item.");
        updatedCart.getItems().forEach(i ->
                System.out.println("   - productId: " + i.getProduct().getId() + ", quantity: " + i.getQuantity())
        );
        return updatedCart;
    }






    @Transactional
    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        ShoppingCartItem itemToRemove = shoppingCartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato nel carrello."));

        // Cancella l'item
        shoppingCartItemRepository.delete(itemToRemove);
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));

        // Salva
        cartRepository.save(cart);

        // LOGGA IL CARRELLO DOPO LA RIMOZIONE
        System.out.println("Dopo removeItem, nel DB il carrello ha " + cart.getItems().size() + " item:");
        cart.getItems().forEach(i ->
                System.out.println("  - productId: " + i.getProduct().getId() + ", quantity=" + i.getQuantity())
        );

        return getCartByUserId(userId); // se stai ricaricando per avere un carrello "pulito"
    }





    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        // Cancella tutti gli item associati al carrello
        shoppingCartItemRepository.deleteAllByCartId(cart.getId());
        // Pulisce la lista degli item nel carrello in memoria
        cart.getItems().clear();
        // Salva il carrello aggiornato
        cartRepository.save(cart);
        // Ricarica il carrello aggiornato (con join fetch, per coerenza)
        return getCartByUserId(userId);
    }

}
