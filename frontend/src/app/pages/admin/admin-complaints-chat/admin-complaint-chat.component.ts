import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe, NgClass } from '@angular/common';

@Component({
  selector: 'app-admin-complaint-chat',
  templateUrl: './admin-complaint-chat.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NgClass,
    DatePipe
  ],
  styleUrls: ['./admin-complaint-chat.component.css']
})
export class AdminComplaintChatComponent implements OnInit, OnChanges {
  @Input() complaintId!: number;

  messages: any[] = [];
  newMessage: string = '';
  isLoading = false;
  error: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    if (this.complaintId) {
      this.loadMessages();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['complaintId'] && !changes['complaintId'].firstChange) {
      this.loadMessages();
    }
  }

  loadMessages(): void {
    if (!this.complaintId) return;

    this.isLoading = true;
    this.error = null;

    this.http.get<any[]>(`http://localhost:8080/api/complaints/${this.complaintId}/messages`)
      .subscribe({
        next: (data) => {
          this.messages = data;
          this.isLoading = false;
        },
        error: (err) => {
          this.error = 'Errore nel caricamento dei messaggi';
          console.error('Errore loadMessages:', err);
          this.isLoading = false;
        }
      });
  }

  sendMessage(): void {
    const content = this.newMessage.trim();
    if (!content) return;

    const payload = {
      sender: 'ADMIN',
      content
    };

    this.http.post(`http://localhost:8080/api/complaints/${this.complaintId}/messages`, payload)
      .subscribe({
        next: () => {
          this.newMessage = '';
          this.loadMessages();
        },
        error: (err) => {
          this.error = 'Errore nell\'invio del messaggio';
          console.error('Errore sendMessage:', err);
        }
      });
  }
}
