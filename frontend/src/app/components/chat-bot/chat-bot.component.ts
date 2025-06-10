import {
  Component,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnDestroy,
  OnInit
} from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.services';
import { RouterLink } from '@angular/router';
import { Subject, throwError } from 'rxjs';
import { takeUntil, catchError, finalize, timeout } from 'rxjs/operators';

interface ChatRequest { userId: number; message: string; }
interface BotButton  { label: string; link: string; }
interface BotReply {
  text: string;
  buttons?: BotButton[];
  intentName?: string;
  confidenceScore?: number;
  fallback?: boolean;
  examples?: string[]; // <--- aggiunto
}

interface Message {
  sender: 'user' | 'bot';
  text: string;
  buttons?: BotButton[];
  intentName?: string;
  confidenceScore?: number;
  fallback?: boolean;
  examples?: string[]; // <--- aggiunto
}


@Component({
  selector: 'app-chat-bot',
  templateUrl: './chat-bot.component.html',
  styleUrls: ['./chat-bot.component.css'],
  standalone: true,
  imports: [
    CommonModule,    // per *ngFor, *ngIf
    FormsModule,     // per [(ngModel)]
    NgClass,
    RouterLink
  ]
})
export class ChatBotComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chatEnd') private chatEnd!: ElementRef;

  userId!: number;
  userMessage = '';
  isLoading   = false;
  messages: Message[] = [];

  /** FAQ fisse in alto */
  faqQuestions = [
    "Dov'è il mio ordine 73?",
    "Mi serve il prodotto motorola",
    "Mostrami il carrello",
    "Voglio parlare con un operatore",
    "Mostrami la mia lista desideri",
    "Quando arriva il mio ordine?",

  ];

  private destroy$ = new Subject<void>();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const uid = this.authService.getCurrentUserId();
    if (uid == null) throw new Error('Utente non autenticato');
    this.userId = uid;

    const saved = localStorage.getItem('chat_messages');
    if (saved) {
      try { this.messages = JSON.parse(saved); }
      catch { this.messages = []; }
    }
  }

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /** Invia una FAQ predefinita */
  sendFAQ(question: string): void {
    this.userMessage = question;
    this.sendMessage();
  }

  sendMessage(): void {
    const text = this.userMessage.trim();
    if (!text) return;

    // Push user message
    this.messages.push({ sender: 'user', text });
    this.saveMessages();

    this.userMessage = '';
    this.isLoading  = true;

    const payload: ChatRequest = { userId: this.userId, message: text };

    this.http.post<BotReply>(
      `http://localhost:8080/api/chatbot/message`,
      payload
    )
      .pipe(
        // timeout(10_000),             // ⬅︎ commenta o alza il valore
        timeout(30_000),                // ad es. 30 s
        takeUntil(this.destroy$),
        catchError(err => this.handleError(err)),
        finalize(() => this.isLoading = false)
      )

      .subscribe(reply => {
        console.log('✅ BOT REPLY:', reply); // <--- AGGIUNGI QUESTO

        const safeText = reply.text ?? '❌ Errore: il bot non ha risposto correttamente.';

        const botMsg: Message = {
          sender: 'bot',
          text: safeText,
          buttons: reply.buttons || [],
          intentName: reply.intentName,
          confidenceScore: reply.confidenceScore,
          fallback: reply.fallback,
          examples: reply.examples || [] // <---
        };


        this.messages.push(botMsg);
        this.saveMessages();
        this.scrollToBottom();
        console.log('Intent:', reply.intentName, 'score:', reply.confidenceScore, 'fallback:', reply.fallback);
      });

  }

  clearChat(): void {
    this.messages = [];
    localStorage.removeItem('chat_messages');
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      try { this.chatEnd.nativeElement.scrollIntoView({ behavior: 'smooth' }); }
      catch {}
    }, 50);
  }

  private saveMessages(): void {
    localStorage.setItem('chat_messages', JSON.stringify(this.messages));
  }

  private handleError(err: HttpErrorResponse) {
    console.error('❌ ChatBot API error', err);

    let msg = 'Errore sconosciuto.';
    if (err.status === 0) {
      msg = '❌ Impossibile raggiungere il server.';
    } else if (typeof err.error === 'string') {
      msg = err.error;
    } else if (err.error?.text) {
      msg = err.error.text;
    } else if (err.message) {
      msg = err.message;
    }

    this.messages.push({ sender: 'bot', text: msg, buttons: [] });
    this.saveMessages();
    this.scrollToBottom();
    return throwError(() => err);
  }

}
