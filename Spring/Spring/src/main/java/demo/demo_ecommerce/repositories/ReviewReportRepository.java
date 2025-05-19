package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    List<ReviewReport> findByReviewId(Long reviewId);
    List<ReviewReport> findAllByOrderByReportedAtDesc();
}

