import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.services';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
  imports: [CommonModule, FormsModule]
})
export class ResetPasswordComponent implements OnInit {
  token: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  successMessage: string = '';
  errorMessage: string = '';
  isSubmitting: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      this.errorMessage = 'Token mancante o non valido.';
    }
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    // ✅ Validazione campi vuoti
    if (!this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Inserisci la nuova password in entrambi i campi.';
      return;
    }

    // ✅ Verifica che le due password coincidano
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Le password non coincidono.';
      return;
    }

    // ✅ Invio della richiesta al backend
    this.isSubmitting = true;

    this.authService.resetPassword(this.token, this.newPassword).subscribe({
      next: (res) => {
        this.successMessage = res;
        this.errorMessage = '';
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Errore durante il reset.';
        this.successMessage = '';
        this.isSubmitting = false;
      }
    });
  }
}
