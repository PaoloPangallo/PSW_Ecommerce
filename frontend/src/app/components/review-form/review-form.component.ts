import { Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.services';
import { ReviewService } from '../../services/review-list.services';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-review-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './review-form.component.html',
  styleUrls: ['./review-form.component.css']
})
export class ReviewFormComponent {
  @Input() productId!: number;
  reviewForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private reviewService: ReviewService,
    private authService: AuthService
  ) {
    this.reviewForm = this.fb.group({
      rating: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  submitReview(): void {
    if (this.reviewForm.invalid) {
      this.reviewForm.markAllAsTouched();
      return;
    }

    // Controllo aggiuntivo per verificare se l'utente ha già recensito
    const currentUserId = this.authService.getCurrentUserId();
    this.reviewService.getReviewsByProductId(this.productId).subscribe({
      next: (reviews) => {
        const alreadyReviewed = reviews.some(review => review.userId === currentUserId);
        if (alreadyReviewed) {
          this.errorMessage = "Hai già recensito questo prodotto.";
          return;
        }

        // Prepara i dati della recensione da inviare al backend
        const reviewData = {
          rating: this.reviewForm.value.rating,
          comment: this.reviewForm.value.comment,
          product: { id: this.productId },
          user: { id: currentUserId }
        };

        this.reviewService.createReview(reviewData).subscribe({
          next: (response) => {
            this.successMessage = "Recensione creata con successo!";
            this.reviewForm.reset();
          },
          error: (err) => {
            this.errorMessage = "Errore nella creazione della recensione.";
            console.error(err);
          }
        });
      },
      error: (err) => {
        this.errorMessage = "Errore nel controllo delle recensioni.";
        console.error(err);
      }
    });
  }
}
