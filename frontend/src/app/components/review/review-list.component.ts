import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ReviewUpvoteComponent } from '../upvote/review-upvote.component'; // <-- percorso corretto
import { ReviewDTO } from '../../models/review.models';
import { ReviewService } from '../../services/review-list.services';
import { AuthService } from '../../services/auth.services';

@Component({
  selector: 'app-review-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    ReviewUpvoteComponent // <-- aggiunto qui
  ],
  templateUrl: './review-list.component.html',
  styleUrls: ['./review-list.component.css']
})
export class ReviewListComponent implements OnInit {
  @Input() productId!: number;
  reviews: ReviewDTO[] = [];
  isLoading = false;
  error: string | null = null;
  currentUserId!: number | null;

  constructor(
    private reviewService: ReviewService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getCurrentUserId();
    this.fetchReviews();
  }

  fetchReviews(): void {
    this.isLoading = true;
    this.reviewService.getReviewsByProductId(this.productId).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Errore nel caricamento delle recensioni';
        this.isLoading = false;
      }
    });
  }

  onUpdateReview(review: ReviewDTO): void {
    // ...
  }

  onDeleteReview(reviewId: number): void {
    // ...
  }
}
