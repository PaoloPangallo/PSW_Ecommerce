package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository; // Aggiunto repository

    public CartService(CartRepository cartRepository,
                       UsersRepository usersRepository,
                       ProductRepository productRepository,
                       ShoppingCartItemRepository shoppingCartItemRepository) { // Aggiunto repository nel costruttore
        this.cartRepository = cartRepository;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
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
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Controlla se il prodotto è già nel carrello
        Optional<ShoppingCartItem> existingItemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItemOpt.isPresent()) {
            // Se il prodotto è già nel carrello, aggiorniamo la quantità
            ShoppingCartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            shoppingCartItemRepository.save(existingItem);
        } else {
            // Se il prodotto non è presente, creiamo un nuovo elemento
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
        Cart cart = getCartByUserId(userId);
        System.out.println(">> Carrello prima dell'update: " + cart.getId() + " con " + cart.getItems().size() + " item.");

        Optional<ShoppingCartItem> itemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (itemOpt.isPresent()) {
            ShoppingCartItem item = itemOpt.get();
            System.out.println(">> Item trovato per productId " + productId + ". Quantità attuale: " + item.getQuantity());
            if (newQuantity <= 0) {
                // Rimuovi l'item se la quantità è zero o negativa
                shoppingCartItemRepository.delete(item);
                cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
                System.out.println(">> Item rimosso per productId " + productId);
            } else {
                // Aggiorna la quantità
                item.setQuantity(newQuantity);
                shoppingCartItemRepository.save(item);
                System.out.println(">> Quantità aggiornata per productId " + productId + " a " + newQuantity);
            }
        } else {
            // Se l'item non esiste e la quantità è positiva, aggiungilo
            if (newQuantity > 0) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
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
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        shoppingCartItemRepository.deleteAllByCartId(cart.getId());
        cartRepository.save(cart);
    }
}
