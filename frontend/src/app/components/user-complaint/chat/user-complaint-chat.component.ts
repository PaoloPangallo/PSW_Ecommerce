import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe, NgClass } from '@angular/common';

@Component({
  selector: 'app-user-complaint-chat',
  standalone: true,
  templateUrl: './user-complaint-chat.component.html',
  styleUrls: ['./user-complaint-chat.component.css'],
  imports: [
    CommonModule,
    FormsModule,
    NgClass,
    DatePipe
  ]
})
export class UserComplaintChatComponent implements OnInit, OnChanges {
  @Input() complaintId!: number;
  @Input() userEmail!: string;

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
    this.isLoading = true;
    this.error = null;

    this.http.get<any[]>(`http://localhost:8080/api/complaints/${this.complaintId}/messages`)
      .subscribe({
        next: (data) => {
          this.messages = data;
          this.isLoading = false;
        },
        error: (err) => {
          this.error = 'Errore nel caricamento della conversazione.';
          console.error(err);
          this.isLoading = false;
        }
      });
  }

  sendMessage(): void {
    const content = this.newMessage.trim();
    if (!content) return;

    const payload = {
      sender: 'USER',
      content
    };

    this.http.post(`http://localhost:8080/api/complaints/${this.complaintId}/messages`, payload)
      .subscribe({
        next: () => {
          this.newMessage = '';
          this.loadMessages(); // Ricarica i messaggi appena aggiornati
        },
        error: (err) => {
          this.error = 'Errore nell\'invio del messaggio.';
          console.error(err);
        }
      });
  }

}
