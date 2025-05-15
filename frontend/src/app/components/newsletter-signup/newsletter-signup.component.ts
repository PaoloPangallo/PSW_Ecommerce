import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NewsletterService } from '../../services/newsletter.service';

@Component({
  selector: 'app-newsletter-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './newsletter-signup.component.html',
  styleUrls: ['./newsletter-signup.component.css']
})
export class NewsletterSignupComponent {
  email: string = '';
  message: string = '';
  error: boolean = false;

  constructor(private newsletterService: NewsletterService) {}






  subscribe(): void {
    if (!this.email.trim()) {
      this.error = true;
      this.message = 'Inserisci una email valida.';
      return;
    }

    this.newsletterService.subscribe(this.email).subscribe({
      next: (res) => {
        this.error = false;
        this.message = '✅ Iscrizione completata! Controlla la tua email per confermare.';
        this.email = '';
      },
      error: (err) => {
        this.error = true;
        this.message = err?.error?.error || '❌ Errore durante l’iscrizione. Riprova più tardi.';
      }


    });
  }
}
