import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.services';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  user: User | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId !== null) {
      this.userService.getUserById(userId).subscribe({
        next: (user: User) => {
          this.user = user;
        },
        error: (err) => {
          this.errorMessage = 'Errore nel caricamento del profilo';
          console.error(err);
        }
      });
    } else {
      this.errorMessage = 'Nessun utente loggato';
    }
  }

  updateProfile(): void {
    if (this.user) {
      console.log('Aggiornamento profilo con dati:', this.user);
      this.userService.updateUser(this.user.id, this.user).subscribe({
        next: (updatedUser) => {
          console.log('Server ha restituito:', updatedUser);
          this.user = updatedUser; // Assicurati di assegnare i dati aggiornati
          this.successMessage = 'Profilo aggiornato con successo!';
          this.errorMessage = '';
        },
        error: (err) => {
          this.errorMessage = 'Errore durante l\'aggiornamento del profilo';
          this.successMessage = '';
          console.error(err);
        }
      });
    }
  }


}
