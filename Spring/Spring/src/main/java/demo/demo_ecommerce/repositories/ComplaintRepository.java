package demo.demo_ecommerce.repositories;

// ✅ ComplaintRepository.java

import demo.demo_ecommerce.entities.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByEmail(String email);
}
