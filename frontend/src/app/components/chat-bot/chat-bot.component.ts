import {
  Component,
  ViewChild,
  ElementRef,
  AfterViewChecked,
  OnInit
} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgClass } from '@angular/common';
import { AuthService } from '../../services/auth.services';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-chat-bot',
  templateUrl: './chat-bot.component.html',
  styleUrls: ['./chat-bot.component.css'],
  standalone: true,
  imports: [FormsModule, NgClass, CommonModule, RouterLink],
})
export class ChatBotComponent implements OnInit, AfterViewChecked {
  userId: number | null = null;
  userMessage = '';
  isLoading = false;
  shouldScroll = false;

  faqQuestions: string[] = [
    "Dov'è il mio ordine 73?",
    "Mi serve il prodotto motorola",
    "Come posso usare un coupon?",
    "Quali sono le opzioni di spedizione?",
    "Come posso fare un reclamo?"
  ];

  messages: { sender: 'user' | 'bot', text: string, buttons?: { label: string, link: string }[] }[] = [];

  @ViewChild('chatEnd') chatEnd!: ElementRef;

  ngOnInit(): void {
    const stored = localStorage.getItem('chat_messages');
    if (stored) {
      this.messages = JSON.parse(stored);
    }
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom(); // Scroll sempre dopo ogni aggiornamento
  }

  constructor(private http: HttpClient, private authService: AuthService) {
    this.userId = this.authService.getCurrentUserId();
  }

  lastBotButtons: { label: string; link: string }[] = [];

  sendMessage() {
    if (!this.userMessage.trim()) return;

    const msg = this.userMessage;
    this.messages.push({ sender: 'user' as const, text: msg }); // ✅ cast 'user' come literal
    this.saveMessages();

    this.userMessage = '';
    this.isLoading = true;

    this.http.post<any>(`http://localhost:8080/api/chatbot/message?userId=${this.userId}`, msg)
      .subscribe({
        next: (botReply) => {
          const message = {
            sender: 'bot' as const, // ✅ cast 'bot' come literal
            text: botReply.text,
            buttons: botReply.buttons || []
          };

          this.messages.push(message);
          this.lastBotButtons = message.buttons || [];
          this.isLoading = false;
          this.saveMessages();
        },
        error: () => {
          const errMsg = {
            sender: 'bot' as const, // ✅ anche qui
            text: '❌ Errore nel contattare il bot.'
          };

          this.messages.push(errMsg);
          this.lastBotButtons = [];
          this.isLoading = false;
          this.saveMessages();
        }
      });
  }





  scrollToBottom() {
    try {
      this.chatEnd.nativeElement.scrollIntoView({ behavior: 'smooth' });
    } catch (err) {
      console.warn('Errore nel fare scroll:', err);
    }
  }

  sendFAQ(question: string) {
    this.userMessage = question;
    this.sendMessage();
  }

  saveMessages() {
    localStorage.setItem('chat_messages', JSON.stringify(this.messages));
  }

  clearChat() {
    this.messages = [];
    localStorage.removeItem('chat_messages');
    this.shouldScroll = true;
  }

  splitResponse(text: string): { llama: string, intent: string | null } {
    if (text.includes("**Suggerimento mirato")) {
      const [llama, intent] = text.split("**Suggerimento mirato");
      return {
        llama: llama.trim(),
        intent: "**Suggerimento mirato" + intent.trim()
      };
    }
    return { llama: text, intent: null };
  }
}
