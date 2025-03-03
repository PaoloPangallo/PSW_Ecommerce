import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private baseUrl = 'http://localhost:8080';  // o un environment, es. environment.apiUrl

  constructor(private http: HttpClient) {}

  // Crea un nuovo ordine per l'utente
  createOrder(userId: number): Observable<Order> {
    // Il body della POST può essere vuoto o contenere parametri aggiuntivi
    return this.http.post<Order>(`${this.baseUrl}/users/${userId}/orders`, {});
  }

  // Recupera tutti gli ordini di un utente
  getOrdersByUser(userId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/users/${userId}/orders`);
  }

  // Recupera i dettagli di un singolo ordine
  getOrderById(userId: number, orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/users/${userId}/orders/${orderId}`);
  }
}
