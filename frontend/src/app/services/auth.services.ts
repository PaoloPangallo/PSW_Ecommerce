// src/app/services/auth.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // URL base per il backend (senza proxy)
  private readonly baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // Login: POST http://localhost:8080/api/auth/login
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, { username, password });
  }

  // Registrazione: POST http://localhost:8080/api/auth/register
  register(userDTO: any): Observable<string> {
    return this.http.post(`${this.baseUrl}/register`, userDTO, { responseType: 'text' });
  }

  // Salva il token (ad esempio in localStorage)
  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // Recupera il token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Rimuove il token (logout)
  removeToken(): void {
    localStorage.removeItem('token');
  }

  // Metodo per il logout (comodo per eventuali altre logiche)
  logout(): void {
    this.removeToken();
  }
}
