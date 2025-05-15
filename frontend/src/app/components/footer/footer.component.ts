import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
  complaintForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.complaintForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      message: ['', Validators.required]
    });
  }

  submitComplaint() {
    if (this.complaintForm.valid) {
      const { email, message } = this.complaintForm.value;
      console.log('📬 Reclamo ricevuto:', { email, message });

      // Simula invio e resetta il form
      this.complaintForm.reset();
      alert('Grazie per il tuo messaggio! Ti contatteremo a breve.');
    }
  }
}
