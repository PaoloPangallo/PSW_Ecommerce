import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { ReviewUpvoteComponent } from '../upvote/review-upvote.component';

import { ReviewDTO } from '../../models/review.models';
import { ReviewService } from '../../services/review-list.services';
import { AuthService } from '../../services/auth.services';
import {UpdateReviewDialogComponent} from '../ConfirmDialog/update-review-dialog.component';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-review-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    ReviewUpvoteComponent,       // Componente per gli upvote
    UpdateReviewDialogComponent,
    MatIcon,
    // Dialog di modifica (se anch'esso è standalone)
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
    private authService: AuthService,
    private dialog: MatDialog
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

  /**
   * Apre una finestra di dialogo per modificare la recensione selezionata.
   */
  onUpdateReview(review: ReviewDTO): void {
    const dialogRef = this.dialog.open(UpdateReviewDialogComponent, {
      width: '500px',
      data: review // Passiamo la recensione da modificare
    });

    // Quando la dialog si chiude, recuperiamo i dati aggiornati (rating, comment) se l'utente ha salvato
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // result = { rating: X, comment: '...' }
        const updatedReview = {
          rating: result.rating,
          comment: result.comment
        };

        // Chiamiamo il service per aggiornare la recensione nel backend
        this.reviewService.updateReview(review.id, updatedReview).subscribe({
          next: (response) => {
            // Aggiorna la recensione anche nella lista locale
            const index = this.reviews.findIndex(r => r.id === review.id);
            if (index !== -1) {
              this.reviews[index] = response;
            }
          },
          error: (err) => {
            console.error('Errore nell\'aggiornamento della recensione', err);
          }
        });
      }
    });
  }

  /**
   * Elimina la recensione dopo una conferma
   */
  onDeleteReview(reviewId: number): void {
    if (confirm("Sei sicuro di voler eliminare questa recensione?")) {
      this.reviewService.deleteReview(reviewId).subscribe({
        next: () => {
          // Rimuoviamo la recensione dalla lista locale
          this.reviews = this.reviews.filter(r => r.id !== reviewId);
        },
        error: (err) => {
          console.error("Errore nella cancellazione della recensione", err);
        }
      });
    }
  }
}
