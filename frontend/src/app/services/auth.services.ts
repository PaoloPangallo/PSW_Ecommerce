// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { CartService } from './cart.service'; // Verifica che il path sia corretto

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;  // Campo aggiunto per l'ID utente
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly baseUrl = 'http://localhost:8080/api/auth';
  private currentUserId: number | null = null;

  constructor(private http: HttpClient, private cartService: CartService) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, { username, password }).pipe(
      tap(response => {
        this.setToken(response.token);
        this.currentUserId = response.userId;
        console.log("User ID ricevuto dal backend:", this.currentUserId);
      })
    );
  }

  register(userDTO: any): Observable<string> {
    return this.http.post(`${this.baseUrl}/register`, userDTO, { responseType: 'text' });
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  removeToken(): void {
    localStorage.removeItem('token');
  }

  getCurrentUserId(): number | null {
    return this.currentUserId;
  }

  // Logout: rimuove il token e resetta il carrello
  logout(): void {
    console.log("Eseguo il logout: rimuovo token e resetto il carrello.");
    this.removeToken();
    this.currentUserId = null;
    this.cartService.resetCart();
    console.log("Logout completato, carrello resettato.");
  }
}
