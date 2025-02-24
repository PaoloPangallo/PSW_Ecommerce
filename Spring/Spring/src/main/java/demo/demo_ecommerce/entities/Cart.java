package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "cart")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingCartItem> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(User user) {
        this.user = user;
    }

    // Aggiunge un prodotto al carrello, aggiornando la quantità se già presente
    public void addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Il prodotto non può essere null e la quantità deve essere maggiore di zero.");
        }

        // Cerchiamo se il prodotto è già presente nel carrello
        ShoppingCartItem existingItem = items.stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Aggiorna la quantità esistente
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Crea un nuovo item e lo aggiunge alla lista
            ShoppingCartItem newItem = new ShoppingCartItem();
            newItem.setCart(this);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            items.add(newItem);
        }
    }

    // Rimuove un prodotto dal carrello dato il suo ID
    public void removeItem(Long productId) {
        items.removeIf(i -> i.getProduct().getId().equals(productId));
    }

    // Svuota il carrello
    public void clear() {
        items.clear();
    }
}
