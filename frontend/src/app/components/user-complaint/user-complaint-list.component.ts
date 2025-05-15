// ✅ user-complaint-list.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.services';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {UserComplaintChatComponent} from './chat/user-complaint-chat.component';

@Component({
  selector: 'app-user-complaint-list',
  standalone: true,
  imports: [CommonModule, DatePipe, FormsModule, UserComplaintChatComponent],
  templateUrl: './user-complaint-list.component.html',
  styleUrls: ['./user-complaint-list.component.css']
})
export class UserComplaintListComponent implements OnInit {
  complaints: any[] = [];
  selectedComplaintId: number | null = null;
  userEmail: string = '';
  error: string | null = null;
  isLoading = false;
  newComplaint = {
    category: '',
    message: ''
  };
  submitting = false;

  constructor(private http: HttpClient, private authService: AuthService) {}

  ngOnInit(): void {
    console.log('✅ COMPONENT MONTATO');
    const email = this.authService.getCurrentUserEmail();
    console.log('📧 EMAIL UTENTE:', email);

    if (!email) {
      this.error = 'Utente non loggato o email non disponibile nel token.';
      return;
    }

    this.userEmail = email;
    this.isLoading = true;

    this.http.get<any[]>(`http://localhost:8080/api/complaints/user`)
      .subscribe({
        next: data => {
          this.complaints = data;
          this.isLoading = false;
        },
        error: err => {
          this.error = 'Errore nel recupero dei reclami';
          console.error(err);
          this.isLoading = false;
        }
      });
  }

  selectComplaint(id: number) {
    this.selectedComplaintId = id;
  }

  submitComplaint(): void {
    if (!this.newComplaint.category || !this.newComplaint.message) return;

    this.submitting = true;
    const payload = {
      category: this.newComplaint.category,
      message: this.newComplaint.message
    };


    this.http.post('http://localhost:8080/api/complaints', payload)
      .subscribe({
        next: () => {
          this.newComplaint = { category: '', message: '' };
          this.loadComplaints();
          this.submitting = false;
        },
        error: err => {
          this.error = 'Errore durante l\'invio del reclamo';
          console.error(err);
          this.submitting = false;
        }
      });
  }

  private loadComplaints(): void {
    this.isLoading = true;
    this.http.get<any[]>(`http://localhost:8080/api/complaints/user`)
      .subscribe({
        next: data => {
          this.complaints = data;
          this.isLoading = false;
        },
        error: err => {
          this.error = 'Errore nel ricaricare i reclami';
          console.error(err);
          this.isLoading = false;
        }
      });
  }




}
