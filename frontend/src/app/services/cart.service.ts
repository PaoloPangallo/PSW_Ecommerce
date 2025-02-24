// src/app/services/cart.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { CartDTO } from '../models/cart.model';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly baseUrl = 'http://localhost:8080/api/cart';

  // Uso BehaviorSubject per avere un valore iniziale e aggiornamenti continui
  private cartSubject = new BehaviorSubject<CartDTO | null>(null);
  cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {}

  getCart(userId: number): Observable<CartDTO> {
    return this.http.get<CartDTO>(`${this.baseUrl}/${userId}`).pipe(
      tap((cart: CartDTO) => this.cartSubject.next(cart))
    );
  }

  addItem(userId: number, productId: number, quantity: number): Observable<CartDTO> {
    const params = new HttpParams()
      .set('productId', productId.toString())
      .set('quantity', quantity.toString());
    return this.http.post<CartDTO>(`${this.baseUrl}/${userId}/add`, null, { params }).pipe(
      tap((cart: CartDTO) => this.cartSubject.next(cart))
    );
  }

  removeItem(userId: number, productId: number): Observable<CartDTO> {
    return this.http.delete<CartDTO>(`${this.baseUrl}/${userId}/items/${productId}`).pipe(
      tap((cart: CartDTO) => this.cartSubject.next(cart))
    );
  }

  clearCart(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${userId}/clear`).pipe(
      tap(() => this.cartSubject.next({ userId, items: [] }))
    );
  }
}
