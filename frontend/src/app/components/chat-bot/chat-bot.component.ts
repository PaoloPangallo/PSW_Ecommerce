import { Component, ViewChild, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgClass } from '@angular/common';

@Component({
  selector: 'app-chat-bot',
  templateUrl: './chat-bot.component.html',
  standalone: true,
  imports: [
    FormsModule,
    NgClass,
    CommonModule
  ],
  styleUrls: ['./chat-bot.component.css']
})
export class ChatBotComponent {
  userId = 1;
  userMessage = '';
  isLoading = false;

  messages: { sender: 'user' | 'bot', text: string }[] = [];

  @ViewChild('chatEnd') chatEnd!: ElementRef;

  constructor(private http: HttpClient) {}

  sendMessage() {
    if (!this.userMessage.trim()) return;

    const msg = this.userMessage;
    this.messages.push({ sender: 'user', text: msg });
    this.userMessage = '';
    this.isLoading = true;

    this.http.post(`http://localhost:8080/api/chatbot/message?userId=${this.userId}`, msg, { responseType: 'text' })
      .subscribe({
        next: (botReply) => {
          this.messages.push({ sender: 'bot', text: botReply });
          this.isLoading = false;
          setTimeout(() => this.scrollToBottom(), 0);
        },
        error: () => {
          this.messages.push({ sender: 'bot', text: '❌ Errore nel contattare il bot.' });
          this.isLoading = false;
        }
      });
  }

  scrollToBottom() {
    this.chatEnd?.nativeElement?.scrollIntoView({ behavior: 'smooth' });
  }

  // Optional: parsing LLaMA + intento
  splitResponse(text: string): { llama: string, intent: string | null } {
    const parts = text.split('**Suggerimento mirato');
    return {
      llama: parts[0].trim(),
      intent: parts[1] ? '**Suggerimento mirato' + parts[1].trim() : null
    };
  }
}
