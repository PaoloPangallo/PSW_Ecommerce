import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ReviewService } from '../../../services/review-list.services';

@Component({
  selector: 'app-admin-reported-reviews',
  standalone: true,
  templateUrl: './admin-reported-reviews.component.html',
  styleUrls: ['./admin-reported-reviews.component.css'],
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ]
})
export class AdminReportedReviewsComponent implements OnInit {
  reports: any[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(private reviewService: ReviewService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.reviewService.getReportedReviews().subscribe({
      next: (res) => {
        this.reports = res;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Errore nel caricamento delle segnalazioni';
        this.isLoading = false;
      }
    });
  }

  deleteReview(reviewId: number): void {
    if (confirm('Vuoi davvero eliminare questa recensione segnalata?')) {
      this.reviewService.deleteReview(reviewId).subscribe(() => {
        this.reports = this.reports.filter(r => r.reviewId !== reviewId);
      });
    }
  }
}
