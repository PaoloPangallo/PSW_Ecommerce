package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.ReviewReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReportDTO {
    private Long id;
    private Long reviewId;
    private String reviewComment;
    private Long userId;
    private String userName;
    private String reason;
    private LocalDateTime reportedAt;

    public static ReviewReportDTO fromEntity(ReviewReport report) {
        return new ReviewReportDTO(
                report.getId(),
                report.getReview().getId(),
                report.getReview().getComment(),
                report.getUser().getId(),
                report.getUser().getUsername(),
                report.getReason(),
                report.getReportedAt()
        );
    }
}

