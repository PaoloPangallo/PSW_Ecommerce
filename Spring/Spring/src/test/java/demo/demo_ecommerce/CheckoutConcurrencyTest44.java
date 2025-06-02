package demo.demo_ecommerce;

import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.repositories.*;
import demo.demo_ecommerce.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class CheckoutConcurrencyTest44 {

    @Autowired private ProductRepository productRepository;
    @Autowired private UsersRepository usersRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private ShoppingCartItemRepository shoppingCartItemRepository;
    @Autowired private OrderService orderService;

    @Test
    void testOptimisticLockExceptionOnConcurrentCheckout() throws InterruptedException {
        // 1. Crea utente
        // 1. Crea utente
        User user = new User("utente1", "utente1@example.com", "password123", Role.USER);

// 2. Crea carrello
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart); // 🔥 IMPORTANTE: relazione bidirezionale

        usersRepository.save(user); // salva tutto in cascata (incluso il cart se configurato)




        // 3. Crea prodotto con stock 10 e version 0
        Product product = new Product();
        product.setName("Prodotto test");
        product.setPrice(new BigDecimal("10.00"));
        product.setStock(10);
        product.setVersion(0L);
        productRepository.save(product);

        // 4. Aggiungi item al carrello
        ShoppingCartItem item = new ShoppingCartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(5);
        shoppingCartItemRepository.save(item);

        // 5. Due thread simulano checkout sullo stesso carrello
        Thread thread1 = new Thread(() -> {
            try {
                orderService.createOrder(user.getId(), Order.ShippingMethod.STANDARD);
            } catch (Exception e) {
                System.out.println("❌ Thread 1 fallito: " + e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(200); // attende che thread1 modifichi il prodotto
                orderService.createOrder(user.getId(), Order.ShippingMethod.STANDARD);
            } catch (Exception e) {
                System.out.println("✅ Thread 2 ha rilevato un conflitto: " + e.getClass().getSimpleName());
                assertTrue(e instanceof ObjectOptimisticLockingFailureException
                        || e.getCause() instanceof ObjectOptimisticLockingFailureException);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }


    @Test
    void testCartModifiedConcurrentlyFailsCheckout() throws InterruptedException {
        // 1. Crea utente e carrello
        User user = new User("utente2", "utente2@example.com", "password123", Role.USER);
        Cart cart = new Cart(user);
        user.setCart(cart);
        usersRepository.save(user);

        // 2. Crea prodotto
        Product product = new Product();
        product.setName("Prodotto X");
        product.setPrice(new BigDecimal("20.00"));
        product.setStock(10);
        product.setVersion(0L);
        productRepository.save(product);

        // 3. Aggiungi item iniziale al carrello
        ShoppingCartItem item = new ShoppingCartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        shoppingCartItemRepository.save(item);

        // 4. Simula due thread
        Thread threadA = new Thread(() -> {
            try {
                Thread.sleep(300); // attende che thread B modifichi il carrello
                orderService.createOrder(user.getId(), Order.ShippingMethod.STANDARD);
                System.out.println("❌ Thread A ha completato il checkout (non previsto)");
            } catch (Exception e) {
                System.out.println("✅ Thread A ha fallito per conflitto: " + e.getClass().getSimpleName());
                assert e instanceof ObjectOptimisticLockingFailureException;
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                Thread.sleep(100); // modifica concorrente prima del salvataggio finale del checkout
                Cart freshCart = cartRepository.findByUserIdWithItems(user.getId()).orElseThrow();
                ShoppingCartItem newItem = new ShoppingCartItem();
                newItem.setCart(freshCart);
                newItem.setProduct(product);
                newItem.setQuantity(1);
                shoppingCartItemRepository.save(newItem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();
    }

}
