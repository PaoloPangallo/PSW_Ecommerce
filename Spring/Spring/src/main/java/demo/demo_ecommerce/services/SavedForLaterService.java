package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.SavedForLaterItem;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.SavedForLaterRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedForLaterService {

    private final SavedForLaterRepository savedRepo;
    private final ProductRepository productRepo;
    private final UsersRepository userRepo;

    public SavedForLaterService(SavedForLaterRepository savedRepo, ProductRepository productRepo, UsersRepository userRepo) {
        this.savedRepo = savedRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public void saveForLater(Long userId, Long productId, int quantity) {
        User user = userRepo.findById(userId).orElseThrow();
        Product product = productRepo.findById(productId).orElseThrow();
        SavedForLaterItem item = savedRepo.findByUserAndProduct(user, product)
                .orElse(SavedForLaterItem.builder().user(user).product(product).build());

        item.setQuantity(quantity);
        savedRepo.save(item);
    }

    public List<SavedForLaterItem> getSavedItems(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        return savedRepo.findByUser(user);
    }
    @Transactional
    public void removeSavedItem(Long userId, Long productId) {
        User user = userRepo.findById(userId).orElseThrow();
        Product product = productRepo.findById(productId).orElseThrow();
        savedRepo.deleteByUserAndProduct(user, product);
    }
}
