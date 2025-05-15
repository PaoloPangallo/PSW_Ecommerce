// src/app/services/newsletter.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NewsletterSubscriber {
  email: string;
  confirmed?: boolean;
  confirmationToken?: string;
  subscriptionDate?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NewsletterService {
  private readonly apiUrl = 'http://localhost:8080/api/newsletter';

  constructor(private http: HttpClient) {}

  subscribe(email: string): Observable<{ message: string, confirmationUrl: string }> {
    return this.http.post<{ message: string, confirmationUrl: string }>(
      `${this.apiUrl}/subscribe`, { email }
    );
  }

  confirm(token: string): Observable<{ message: string }> {
    return this.http.get<{ message: string }>(
      `${this.apiUrl}/confirm?token=${token}`
    );
  }


  getAllSubscribers(): Observable<NewsletterSubscriber[]> {
    return this.http.get<NewsletterSubscriber[]>(`${this.apiUrl}/all`);
  }

  sendNow(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/send-now`, {});
  }


}
