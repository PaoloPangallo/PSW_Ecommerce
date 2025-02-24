import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.services'; // Assicurati che il path sia corretto
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
  email = ''; // Campo email per la registrazione
  password = '';
  errorMessage = '';
  successMessage = '';
  isRegisterMode: boolean = false; // false = login, true = registrazione

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
      // Modalità registrazione
      const userDTO = {
        username: this.username,
        email: this.email,
        password: this.password
      };
      this.authService.register(userDTO).subscribe({
        next: (response) => {
          this.successMessage = response; // Messaggio dal backend (es. "User registered successfully")
          this.errorMessage = '';
          // Puoi, ad esempio, resettare i campi del form
          this.username = '';
          this.email = '';
          this.password = '';
          // Dopo un po', passa alla modalità login
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
      // Modalità login
      this.authService.login(this.username, this.password).subscribe({
        next: (res) => {
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
  }
}
