package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.ResourceNotFoundException;
import demo.demo_ecommerce.entities.Review;
import demo.demo_ecommerce.entities.ReviewReport;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.ReviewReportRepository;
import demo.demo_ecommerce.repositories.ReviewRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewResetService {

    private final ReviewReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ReviewResetService(ReviewReportRepository reportRepository, ReviewRepository reviewRepository, UsersRepository usersRepository) {
        this.reportRepository = reportRepository;
        this.reviewRepository = reviewRepository;
        this.usersRepository = usersRepository;
    }

    public void reportReview(Long reviewId, Long userId, String reason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ReviewReport report = ReviewReport.builder()
                .review(review)
                .user(user)
                .reason(reason)
                .build();

        reportRepository.save(report);
    }

    public List<ReviewReport> getAllReports() {
        return reportRepository.findAllByOrderByReportedAtDesc();
    }
}

