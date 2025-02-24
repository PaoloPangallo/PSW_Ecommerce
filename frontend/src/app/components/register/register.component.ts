import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.services'; // Verifica il path corretto

@Component({
  standalone: true,
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  imports: [CommonModule, FormsModule]
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    // Crei l'oggetto userDTO con i dati del form
    const userDTO = {
      username: this.username,
      email: this.email,
      password: this.password
    };

    // Chiami l'endpoint di registrazione
    this.authService.register(userDTO).subscribe({
      next: (response) => {
        // Il backend potrebbe restituire un messaggio di successo
        this.successMessage = response; // "User registered successfully"
        // Dopo un po' di tempo, potresti reindirizzare al login
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        // Se il backend risponde con un errore (ad es. "Username already exists"),
        // lo mostri qui
        this.errorMessage = err.error || err.message;
      }
    });
  }
}
