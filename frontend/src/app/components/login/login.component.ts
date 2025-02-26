// src/app/components/login/login.component.ts
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
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  isRegisterMode: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  toggleMode(): void {
    this.isRegisterMode = !this.isRegisterMode;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onSubmit(): void {
    if (this.isRegisterMode) {
      const userDTO = {
        username: this.username,
        email: this.email,
        password: this.password
      };
      this.authService.register(userDTO).subscribe({
        next: (response) => {
          this.successMessage = response;
          this.errorMessage = '';
          this.username = '';
          this.email = '';
          this.password = '';
          setTimeout(() => {
            this.toggleMode();
            this.successMessage = '';
          }, 3000);
        },
        error: (err) => {
          this.errorMessage = err.error || 'Errore durante la registrazione';
          this.successMessage = '';
        }
      });
    } else {
      this.authService.login(this.username, this.password).subscribe({
        next: (res) => {
          console.log("Login riuscito, token ricevuto:", res.token);
          this.authService.setToken(res.token);
          this.errorMessage = '';
          // Puoi eventualmente richiamare loadCart qui se hai accesso al CartService, oppure nella dashboard
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.errorMessage = 'Credenziali non valide';
          this.successMessage = '';
        }
      });
    }
  }
}
