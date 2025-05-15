import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.services';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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
        next: (serverUser: any) => {
          // Mappiamo i dati dal server (in inglese) al nostro modello User
          this.user = this.mapServerUser(serverUser);
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
      // Mappiamo l'oggetto user nel formato richiesto dal server
      const serverUserPayload = this.mapUserToServerUser(this.user);
      this.userService.updateUser(this.user.id, serverUserPayload).subscribe({
        next: (updatedServerUser: any) => {
          console.log('Server ha restituito:', updatedServerUser);
          // Aggiorniamo l'oggetto user mappando i dati restituiti dal server
          this.user = this.mapServerUser(updatedServerUser);
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

  /**
   * Mappa i dati provenienti dal server (che ora usa nomi in inglese)
   * nel nostro modello User (stesso naming in inglese).
   */
  private mapServerUser(serverUser: any): User {
    return {
      id: serverUser.id,
      username: serverUser.username,
      email: serverUser.email,
      role: serverUser.role,
      phone: serverUser.phone,       // <-- Usiamo 'phone', NON 'telefono'
      address: serverUser.address,   // <-- Usiamo 'address', NON 'indirizzo'
      cap: serverUser.cap,
      city: serverUser.city,         // <-- Usiamo 'city', NON 'citta'
      region: serverUser.region,     // <-- Usiamo 'region', NON 'regione'
      country: serverUser.country    // <-- Usiamo 'country', NON 'paese'
    };
  }

  /**
   * Mappa l'oggetto User nel formato richiesto dal server per l'update,
   * ovvero con campi in inglese.
   */
  private mapUserToServerUser(user: User): any {
    return {
      email: user.email,
      phone: user.phone,
      address: user.address,
      cap: user.cap,
      city: user.city,
      region: user.region,
      country: user.country
    };
  }
}
