import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {Order, Page} from '../models/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // Recupera la lista di ordini paginati per l'utente
  getOrdersByUserPaginated(userId: number, page: number, size: number): Observable<Page<Order>> {
    // Creiamo i parametri ?page=...&size=...
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<Page<Order>>(
      `${this.baseUrl}/users/${userId}/orders`,
      { params }
    );
  }

  // Recupera i dettagli di un singolo ordine
  getOrderById(userId: number, orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/users/${userId}/orders/${orderId}`);
  }


  // Aggiungi il metodo per creare un ordine
  createOrder(userId: number): Observable<Order> {
    return this.http.post<Order>(`${this.baseUrl}/users/${userId}/orders`, {});
  }

}
