import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.services';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, FormsModule]
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  showForgotPassword = false;
  email = '';
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    protected router: Router
  ) {}

  onSubmit(): void {
    this.authService.login(this.username, this.password).subscribe({
      next: (res) => {
        console.log("Login riuscito, token ricevuto:", res.token);
        this.authService.setToken(res.token);
        this.errorMessage = '';
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = 'Credenziali non valide';
        this.successMessage = '';
      }
    });
  }

  toggleForgotPassword(): void {
    this.showForgotPassword = !this.showForgotPassword;
    this.errorMessage = '';
    this.successMessage = '';
  }

  submitForgotPassword(): void {
    if (!this.email) {
      this.errorMessage = 'Inserisci una email valida';
      return;
    }

    this.isSubmitting = true;
    this.authService.requestPasswordReset(this.email).subscribe({
      next: (res: any) => {
        console.log('Risposta backend:', res);
        this.successMessage = res.message || '✅ Se l\'email è corretta, riceverai un link per reimpostare la password.';
        this.errorMessage = '';
        this.isSubmitting = false;
      },
      error: (err) => {
        console.error('Errore nel reset:', err);
        this.errorMessage = err.error?.message || 'Errore durante l\'invio';
        this.successMessage = '';
        this.isSubmitting = false;
      }
    });
  }

}
