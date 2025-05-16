package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}

