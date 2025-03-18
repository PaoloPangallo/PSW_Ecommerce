import { Component } from '@angular/core';
import { FirebaseStorageService } from '../../../service-admin/firebase-storage.service';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-admin-home-photo',
  templateUrl: './admin-home-photo.component.html',
  standalone: true,
  styleUrls: ['./admin-home-photo.component.css'],
  imports: [CommonModule]
})
export class AdminHomePhotoComponent {
  selectedFile: File | null = null;
  imageUrl: string = '';
  loading = false;
  errorMsg = '';

  constructor(private firebaseStorageService: FirebaseStorageService) {
  }

  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.selectedFile = target.files[0];
    }
  }

  onUpload(): void {
    if (!this.selectedFile) {
      this.errorMsg = 'Seleziona un file da caricare';
      return;
    }
    this.loading = true;
    this.errorMsg = '';
    this.firebaseStorageService.uploadFile(this.selectedFile).subscribe({
      next: (url) => {
        this.imageUrl = url;
        this.loading = false;
      },
      error: () => {
        this.errorMsg = 'Errore durante l\'upload';
        this.loading = false;
      }
    });
  }


}
