package demo.demo_ecommerce.repositories;


import demo.demo_ecommerce.entities.ComplaintMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintMessageRepository extends JpaRepository<ComplaintMessage, Long> {
    List<ComplaintMessage> findByComplaintIdOrderByTimestampAsc(Long complaintId);
}

