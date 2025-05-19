import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogModule,
  MatDialogRef
} from '@angular/material/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { ReviewService } from '../../services/review-list.services';
import { AuthService } from '../../services/auth.services';
import {MatFormField, MatFormFieldModule} from '@angular/material/form-field';
import {CommonModule} from '@angular/common';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';

@Component({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  selector: 'app-review-report-dialog',
  standalone: true,
  templateUrl: './review-report-dialog.component.html'
})
export class ReviewReportDialogComponent {
  reportForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ReviewReportDialogComponent>,
    private reviewService: ReviewService,
    private authService: AuthService,
    @Inject(MAT_DIALOG_DATA) public data: { reviewId: number }
  ) {
    this.reportForm = this.fb.group({
      reason: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  submit(): void {
    if (this.reportForm.invalid) return;

    const reason = this.reportForm.value.reason;
    const userId = this.authService.getCurrentUserId();
    if (userId === null) {
      console.error('Utente non autenticato. Impossibile segnalare la recensione.');
      return;
    }

    this.reviewService.reportReview(this.data.reviewId, userId, reason).subscribe({
      next: () => this.dialogRef.close(),
      error: err => {
        console.error('Errore durante la segnalazione:', err);
        this.dialogRef.close();
      }
    });
  }


  cancel(): void {
    this.dialogRef.close();
  }
}
