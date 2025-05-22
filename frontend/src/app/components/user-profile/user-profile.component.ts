import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.services';
import { UserService } from '../../services/user.service';
import { User, UserProfileSummary } from '../../models/user.model';
import { RouterLink } from '@angular/router';
import { ReviewDTO } from '../../models/review.models';

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

  reviews: ReviewDTO[] = [];
  reviewsPage = 0;
  hasMoreReviews = false;

  private userId: number | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUserId();

    if (this.userId !== null) {
      this.loadUserReviews(this.userId);

      this.userService.getUserById(this.userId).subscribe({
        next: (serverUser) => {
          this.user = this.mapServerUser(serverUser);
        },
        error: (err) => {
          this.errorMessage = 'Errore nel caricamento del profilo';
          console.error(err);
        }
      });

      this.userService.getUserProfileSummary(this.userId).subscribe({
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
    if (this.user && this.userId !== null) {
      const serverUserPayload = this.mapUserToServerUser(this.user);
      this.userService.updateUser(this.userId, serverUserPayload).subscribe({
        next: (updatedServerUser) => {
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

  onImageSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file && this.userId !== null) {
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(file);

      const formData = new FormData();
      formData.append('file', file);

      this.userService.uploadProfileImage(this.userId, formData).subscribe({
        next: (imageUrl: string) => {
          if (this.user) {
            this.user.profileImageUrl = imageUrl;
            this.previewUrl = null;
          }
        },
        error: (err) => {
          console.error('Errore durante il caricamento dell\'immagine', err);
        }
      });
    }
  }

  removeProfileImage(): void {
    const dialog = document.querySelector('dialog') as HTMLDialogElement;
    dialog?.showModal();
  }

  confirmRemove(): void {
    if (this.user && this.userId !== null) {
      this.user.profileImageUrl = undefined;
      this.previewUrl = null;

      this.userService.removeProfileImage(this.userId).subscribe({
        next: () => {
          this.successMessage = 'Foto profilo rimossa con successo!';
          this.errorMessage = '';
          const dialog = document.querySelector('dialog') as HTMLDialogElement;
          dialog?.close();
        },
        error: (err) => {
          this.errorMessage = 'Errore durante la rimozione dell\'immagine';
          this.successMessage = '';
          console.error(err);
          const dialog = document.querySelector('dialog') as HTMLDialogElement;
          dialog?.close();
        }
      });
    }
  }

  cancelRemove(): void {
    const dialog = document.querySelector('dialog') as HTMLDialogElement;
    dialog?.close();
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = 'assets/icons/default-avatar.svg';
  }

  loadUserReviews(userId: number): void {
    this.userService.getUserReviews(userId, this.reviewsPage).subscribe({
      next: (res) => {
        this.reviews.push(...res.content);
        this.hasMoreReviews = !res.last;
      },
      error: (err) => {
        console.error('Errore nel caricamento delle recensioni', err);
      }
    });
  }

  loadMoreReviews(): void {
    if (this.userId !== null && this.hasMoreReviews) {
      this.reviewsPage++;
      this.loadUserReviews(this.userId);
    }
  }

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
      profileImageUrl: serverUser.profileImageUrl
    };
  }

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
