package demo.demo_ecommerce.repositories;

// NewsletterSubscriberRepository.java

import demo.demo_ecommerce.entities.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {
    Optional<NewsletterSubscriber> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<NewsletterSubscriber> findByConfirmationToken(String token);

}
