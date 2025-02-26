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
        // Reindirizza alla dashboard o alla homepage dopo il login
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = 'Credenziali non valide';
        this.successMessage = '';
      }
    });
  }
}
