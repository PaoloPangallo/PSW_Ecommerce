import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.services';
import { UserService } from '../../services/user.service';
import {User, UserProfileSummary} from '../../models/user.model';
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
  summary: UserProfileSummary | null = null;
  errorMessage = '';
  successMessage = '';
  previewUrl: string | ArrayBuffer | null = null;


  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId !== null) {

      // Carica dati anagrafici
      this.userService.getUserById(userId).subscribe({
        next: (serverUser: any) => {
          this.user = this.mapServerUser(serverUser);
        },
        error: (err) => {
          this.errorMessage = 'Errore nel caricamento del profilo';
          console.error(err);
        }
      });

      // Carica riepilogo statistico
      this.userService.getUserProfileSummary(userId).subscribe({
        next: (summary) => {
          this.summary = summary;
        },
        error: (err) => {
          console.error('Errore nel caricamento delle statistiche utente', err);
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
      phone: serverUser.phone,
      address: serverUser.address,
      cap: serverUser.cap,
      city: serverUser.city,
      region: serverUser.region,
      country: serverUser.country,
      profileImageUrl: serverUser.profileImageUrl // ✅ aggiunto!
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

  onImageSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      // Preview immediata
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(file);

      const formData = new FormData();
      formData.append('file', file);

      const userId = this.authService.getCurrentUserId();
      if (userId) {
        this.userService.uploadProfileImage(userId, formData).subscribe({
          next: (imageUrl: string) => {
            if (this.user) {
              this.user.profileImageUrl = imageUrl;
              this.previewUrl = null; // resetta dopo l'upload
            }
          },
          error: (err) => {
            console.error('Errore durante il caricamento dell\'immagine', err);
          }
        });
      }
    }

  }
  removeProfileImage(): void {
    if (this.user) {
      this.user.profileImageUrl = undefined;
      this.previewUrl = null;

      // Notifica il backend se vuoi salvare la rimozione nel DB
      this.userService.removeProfileImage(this.user.id).subscribe({
        next: () => console.log('Immagine rimossa'),
        error: err => console.error('Errore nella rimozione immagine', err)
      });
    }
  }





}
