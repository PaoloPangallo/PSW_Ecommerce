import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { UpvoteService } from '../../services/upvote.service';

@Component({
  selector: 'app-review-upvote', // <--- deve corrispondere al tag usato nel template
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review-upvote.component.html',
  styleUrls: ['./review-upvote.component.css']
})
export class ReviewUpvoteComponent implements OnInit {
  @Input() reviewId!: number;
  @Input() userId!: number;
  @Input() initialUpvotes!: number;
  @Input() hasUpvotedInitial!: boolean;

  upvotes!: number;
  hasUpvoted!: boolean;
  error: string = '';

  constructor(private upvoteService: UpvoteService) {}

  ngOnInit(): void {
    this.upvotes = this.initialUpvotes;
    this.hasUpvoted = this.hasUpvotedInitial;
  }

  handleUpvote(): void {
    if (this.hasUpvoted) {
      console.warn('L\'utente ha già votato. Nessuna azione intrapresa.');
      return;
    }

    this.upvoteService.addUpvote(this.reviewId, this.userId).subscribe({
      next: (response) => {
        console.log('Upvote OK:', response);
        this.upvotes++;
        this.hasUpvoted = true;
        this.error = ''; // Reset del messaggio di errore in caso di successo
      },
      error: (err: HttpErrorResponse) => {
        console.error('Upvote ERROR:', err);
        this.error = err.error?.message || 'Errore durante l\'aggiunta dell\'upvote';
      }
    });



  this.upvoteService.addUpvote(this.reviewId, this.userId).subscribe({
      next: () => {
        this.upvotes++;
        this.hasUpvoted = true;
      },
      error: (err: HttpErrorResponse) => {
        this.error = err.error?.message || 'Errore durante l\'aggiunta dell\'upvote';
      }
    });
  }
}
