import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
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
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private cartService: CartService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    if (this.isBrowser) {
      const storedUserId = localStorage.getItem('userId');
      if (storedUserId) {
        this.currentUserId = parseInt(storedUserId, 10);
      }
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, { username, password }).pipe(
      tap(response => {
        if (this.isBrowser) {
          this.setToken(response.token);
          this.currentUserId = response.userId;
          localStorage.setItem('userId', response.userId.toString());
        }
      })
    );
  }

  register(userDTO: any): Observable<string> {
    return this.http.post(`${this.baseUrl}/register`, userDTO, { responseType: 'text' });
  }

  forgotPassword(email: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/forgot-password`, null, {
      params: { email },
      responseType: 'text'
    });
  }

  resetPassword(token: string, newPassword: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/reset-password`, {
      token,
      newPassword
    }, {
      responseType: 'text'
    });
  }


  setToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem('token', token);
    }
  }

  getToken(): string | null {
    return this.isBrowser ? localStorage.getItem('token') : null;
  }

  removeToken(): void {
    if (this.isBrowser) {
      localStorage.removeItem('token');
    }
  }

  getCurrentUserId(): number | null {
    if (this.isBrowser) {
      const storedUserId = localStorage.getItem('userId');
      return storedUserId ? parseInt(storedUserId, 10) : null;
    }
    return null;
  }

  logout(): void {
    if (this.isBrowser) {
      this.removeToken();
      localStorage.removeItem('userId');
    }
    this.currentUserId = null;
    this.cartService.resetCart();
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  isAdmin(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
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
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || payload.email || null;
    } catch (error) {
      console.error('Errore nel leggere l\'email dal token:', error);
      return null;
    }
  }

  requestPasswordReset(email: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/forgot-password`, { email }, {
      responseType: 'text' as 'json'  // 👈 cast per ingannare il tipo
    }) as Observable<string>;         // 👈 cast finale
  }


}
