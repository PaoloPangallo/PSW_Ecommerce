import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class AdminUsersService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  // Recupera tutti gli utenti con paginazione
  getUsers(): Observable<Page<User>> {
    return this.http.get<Page<User>>(this.apiUrl);
  }

  // Aggiorna un utente; updateData contiene i campi da aggiornare
  updateUser(id: number, updateData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, updateData);
  }

  // Elimina un utente
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
