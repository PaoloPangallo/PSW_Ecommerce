import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReviewDTO } from '../../models/review.models';

@Component({
  selector: 'app-update-review-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  template: `
    <h1 mat-dialog-title>Modifica Recensione</h1>
    <div mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Rating</mat-label>
          <input matInput type="number" formControlName="rating" min="1" max="5">
          <mat-error *ngIf="form.controls['rating'].invalid">
            Il rating deve essere compreso tra 1 e 5
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Commento</mat-label>
          <textarea matInput formControlName="comment" rows="4"></textarea>
          <mat-error *ngIf="form.controls['comment'].invalid">
            Il commento è obbligatorio e non può superare 1000 caratteri
          </mat-error>
        </mat-form-field>
      </form>
    </div>
    <div mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-button color="primary" (click)="onSave()" [disabled]="form.invalid">Salva</button>
    </div>
  `,
  styles: [`
    .full-width {
      width: 100%;
    }
  `]
})
export class UpdateReviewDialogComponent {
  form: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<UpdateReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReviewDTO,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({
      rating: [data.rating, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: [data.comment, [Validators.required, Validators.maxLength(1000)]]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.form.valid) {
      // Chiudiamo il dialog ritornando i dati aggiornati
      this.dialogRef.close(this.form.value);
    }
  }
}
