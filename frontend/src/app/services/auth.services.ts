import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { CartService } from './cart.service';

export interface LoginResponse {
  token: string;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly baseUrl = 'http://localhost:8080/api/auth';
  private currentUserId: number | null = null;

  constructor(private http: HttpClient, private cartService: CartService) {
    // Verifica se localStorage è disponibile
    if (typeof localStorage !== 'undefined') {
      const storedUserId = localStorage.getItem('userId');
      if (storedUserId) {
        this.currentUserId = parseInt(storedUserId, 10);
      }
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, { username, password }).pipe(
      tap(response => {
        this.setToken(response.token);
        this.currentUserId = response.userId;
        if (typeof localStorage !== 'undefined') {
          localStorage.setItem('userId', response.userId.toString());
        }
        console.log("User ID ricevuto dal backend:", this.currentUserId);
      })
    );
  }

  register(userDTO: any): Observable<string> {
    return this.http.post(`${this.baseUrl}/register`, userDTO, { responseType: 'text' });
  }

  setToken(token: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('token', token);
    }
  }

  getToken(): string | null {
    return typeof localStorage !== 'undefined' ? localStorage.getItem('token') : null;
  }

  removeToken(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('token');
    }
  }

  getCurrentUserId(): number | null {
    if (this.currentUserId === null && typeof localStorage !== 'undefined') {
      const storedUserId = localStorage.getItem('userId');
      if (storedUserId) {
        this.currentUserId = parseInt(storedUserId, 10);
      }
    }
    return this.currentUserId;
  }

  logout(): void {
    console.log("Eseguo il logout: rimuovo token e resetto il carrello.");
    this.removeToken();
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('userId');
    }
    this.currentUserId = null;
    this.cartService.resetCart();
    console.log("Logout completato, carrello resettato.");
  }

  /**
   * Verifica se l'utente è loggato controllando se esiste un token.
   */
  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  /**
   * Decodifica il token JWT e verifica se il claim "role" è "ADMIN".
   */
  isAdmin(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      // Il token JWT è composto da tre parti: header.payload.signature
      const payloadPart = token.split('.')[1];
      const payloadJson = atob(payloadPart);
      const payload = JSON.parse(payloadJson);
      // Controlla che il ruolo sia "ADMIN"
      return payload.role === 'ADMIN';
    } catch (error) {
      console.error('Errore nel decodificare il token', error);
      return false;
    }
  }
  getCurrentUserEmail(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payloadPart = token.split('.')[1];
      const payloadJson = atob(payloadPart);
      const payload = JSON.parse(payloadJson);
      return payload.sub || payload.email || null;
    } catch (error) {
      console.error('Errore nel leggere l\'email dal token:', error);
      return null;
    }
  }

}
