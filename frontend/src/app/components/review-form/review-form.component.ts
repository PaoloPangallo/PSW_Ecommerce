import { Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.services';
import { ReviewService } from '../../services/review-list.services';
import { CommonModule } from '@angular/common';
import { ReviewDTO } from '../../models/review.models';

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

  selectedImages: File[] = [];
  imagePreviews: string[] = [];

  successMessage: string = '';
  errorMessage: string = '';
  isSubmitting: boolean = false;

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

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const files = Array.from(input.files);
      this.selectedImages = [];

      this.imagePreviews = [];

      for (const file of files) {
        if (file.type.startsWith('image/') && file.size <= 2 * 1024 * 1024) {
          this.selectedImages.push(file);

          // Per preview
          const reader = new FileReader();
          reader.onload = () => this.imagePreviews.push(reader.result as string);
          reader.readAsDataURL(file);
        } else {
          this.errorMessage = 'Solo immagini JPG/PNG sotto i 2MB sono supportate.';
        }
      }
    }
  }

  submitReview(): void {
    if (this.reviewForm.invalid || this.productId == null) {
      this.reviewForm.markAllAsTouched();
      return;
    }

    const currentUserId = this.authService.getCurrentUserId();
    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Verifica se l'utente ha già recensito
    this.reviewService.getReviewsByProductId(this.productId).subscribe({
      next: (reviews: ReviewDTO[]) => {
        const alreadyReviewed = reviews.some(r => r.userId === currentUserId);
        if (alreadyReviewed) {
          this.errorMessage = "Hai già recensito questo prodotto.";
          this.isSubmitting = false;
          return;
        }

        // Step 1 – salva la recensione
        const reviewData = {
          rating: this.reviewForm.value.rating,
          comment: this.reviewForm.value.comment,
          product: { id: this.productId },
          user: { id: currentUserId }
        };

        this.reviewService.createReview(reviewData).subscribe({
          next: (createdReview: ReviewDTO) => {
            // Step 2 – carica immagini se presenti
            if (this.selectedImages.length > 0) {
              this.reviewService.uploadReviewImages(createdReview.id!, this.selectedImages).subscribe({
                next: () => {
                  this.successMessage = 'Recensione creata con successo con immagini!';
                  this.resetForm();
                },
                error: (err) => {
                  console.error(err);
                  this.successMessage = 'Recensione salvata, ma errore durante l\'upload delle immagini.';
                  this.resetForm();
                }
              });
            } else {
              this.successMessage = 'Recensione creata con successo!';
              this.resetForm();
            }
          },
          error: (err) => {
            console.error(err);
            this.errorMessage = 'Errore nella creazione della recensione.';
            this.isSubmitting = false;
          }
        });
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Errore nel controllo delle recensioni esistenti.';
        this.isSubmitting = false;
      }
    });
  }

  private resetForm(): void {
    this.reviewForm.reset();
    this.selectedImages = [];
    this.imagePreviews = [];
    this.isSubmitting = false;
  }
}
