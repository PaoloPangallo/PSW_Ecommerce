package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.SavedForLaterItem;
import demo.demo_ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedForLaterRepository extends JpaRepository<SavedForLaterItem, Long> {
    List<SavedForLaterItem> findByUser(User user);
    Optional<SavedForLaterItem> findByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
}
