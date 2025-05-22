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

@Component({
  selector: 'app-chat-bot',
  templateUrl: './chat-bot.component.html',
  styleUrls: ['./chat-bot.component.css'],

  standalone: true,
  imports: [FormsModule, NgClass, CommonModule],
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

  messages: { sender: 'user' | 'bot', text: string }[] = [];

  @ViewChild('chatEnd', { static: false }) chatEnd!: ElementRef;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.userId = this.authService.getCurrentUserId();
  }

  ngOnInit(): void {
    const stored = localStorage.getItem('chat_messages');
    if (stored) {
      this.messages = JSON.parse(stored);
    }
  }

  ngAfterViewInit(): void {
    // Scroll alla prima apertura
    this.shouldScroll = true;
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  sendMessage() {
    if (!this.userMessage.trim()) return;

    const msg = this.userMessage;
    this.messages.push({ sender: 'user', text: msg });
    this.saveMessages();

    this.userMessage = '';
    this.isLoading = true;
    this.shouldScroll = true;

    this.http.post(`http://localhost:8080/api/chatbot/message?userId=${this.userId}`, msg, { responseType: 'text' })
      .subscribe({
        next: (botReply) => {
          this.messages.push({ sender: 'bot', text: botReply });
          this.isLoading = false;
          this.saveMessages();
          this.shouldScroll = true;
        },
        error: () => {
          this.messages.push({ sender: 'bot', text: '❌ Errore nel contattare il bot.' });
          this.isLoading = false;
          this.saveMessages();
          this.shouldScroll = true;
        }
      });
  }

  sendFAQ(question: string) {
    this.userMessage = question;
    this.sendMessage();
  }
  scrollToBottom() {
    try {
      this.chatEnd?.nativeElement?.scrollIntoView({ behavior: 'smooth' });
    } catch (e) {
      // può capitare alla prima apertura
      console.warn('chatEnd non disponibile ancora', e);
    }
  }

  saveMessages() {
    localStorage.setItem('chat_messages', JSON.stringify(this.messages));
  }

  // Optional: parsing LLaMA + intento
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
