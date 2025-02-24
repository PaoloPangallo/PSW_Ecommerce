import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users'; // ✅ Aggiunto `/api/` per matchare il backend

  constructor(private http: HttpClient) {}

  // ✅ Ottiene tutti gli utenti con paginazione
  getAllUsers(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?page=${page}&size=${size}`).pipe(
      catchError(this.handleError)
    );
  }

  // ✅ Ottiene un singolo utente per ID
  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // ✅ Crea un nuovo utente
  createUser(user: Partial<User>): Observable<User> {
    return this.http.post<User>(this.apiUrl, user, this.httpOptions()).pipe(
      catchError(this.handleError)
    );
  }

  // ✅ Aggiorna un utente
  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user, this.httpOptions()).pipe(
      catchError(this.handleError)
    );
  }

  // ✅ Elimina un utente
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // ✅ Opzioni HTTP per richieste con JSON
  private httpOptions() {
    return {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
  }

  // ✅ Gestione degli errori HTTP
  private handleError(error: any): Observable<never> {
    console.error('Errore HTTP:', error);
    return throwError(() => new Error(error.message || 'Errore del server'));
  }
}
