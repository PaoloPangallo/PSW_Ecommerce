package demo.demo_ecommerce.bot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    // Trova tutti i log con una correzione umana dell'intent
    List<ChatLog> findByIntentCorrectedIsNotNull();

        List<ChatLog> findByUserIdOrderByTimestampDesc(Long userId);

}
