import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { CartDTO } from '../models/cart.model';
import { tap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly baseUrl = 'http://localhost:8080/api/cart';

  // Stato del carrello come BehaviorSubject.
  private cartSubject = new BehaviorSubject<CartDTO | null>(null);
  cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Ottiene il carrello per l'utente
  getCart(userId: number): Observable<CartDTO> {
    return this.http.get<CartDTO>(`${this.baseUrl}/${userId}`).pipe(
      tap(cart => {
        console.log('🛒 Carrello caricato:', cart);
        this.cartSubject.next(cart);
      }),
      catchError(error => this.handleError(error))
    );
  }

  updateItemQuantity(userId: number, productId: number, quantity: number): Observable<CartDTO> {
    return this.http.patch<CartDTO>(
      `${this.baseUrl}/${userId}/items/${productId}`,
      { quantity }
    ).pipe(
      tap(cart => {
        console.log("✅ Carrello aggiornato (updateItemQuantity):", cart);
        this.cartSubject.next(cart);
      }),
      catchError(error => this.handleError(error))
    );
  }

  removeItem(userId: number, productId: number): Observable<CartDTO> {
    return this.http.delete<CartDTO>(`${this.baseUrl}/${userId}/items/${productId}`).pipe(
      tap(cart => {
        console.log("✅ Prodotto rimosso:", cart);
        this.cartSubject.next(cart);
      }),
      catchError(error => this.handleError(error))
    );
  }

  clearCart(userId: number): Observable<CartDTO> {
    return this.http.delete<CartDTO>(`${this.baseUrl}/${userId}/clear`).pipe(
      tap(cart => {
        console.log("🗑️ Carrello svuotato:", cart);
        this.cartSubject.next(cart);
      }),
      catchError(error => this.handleError(error))
    );
  }

  resetCart(): void {
    console.log("Resetto il carrello nel CartService.");
    this.cartSubject.next(null);
  }

  // Nuovo metodo per applicare un coupon a un item del carrello
  applyCouponToItem(cartItemId: number, couponCode: string): Observable<CartDTO> {
    return this.http.post<CartDTO>(
      `${this.baseUrl}/apply-coupon-to-item/${cartItemId}/${couponCode}`,
      {}
    ).pipe(
      tap(cart => {
        console.log("✅ Coupon applicato, carrello aggiornato:", cart);
        this.cartSubject.next(cart);
      }),
      catchError(error => this.handleError(error))
    );
  }

  private handleError(error: any): Observable<never> {
    console.error("❌ Errore HTTP:", error);
    const errorMsg = error?.error?.message || "Si è verificato un errore imprevisto.";
    return throwError(() => new Error(errorMsg));
  }

  addToCart(userId: number, productId: number, quantity: number): Observable<CartDTO> {
    return this.http.post<CartDTO>(`${this.baseUrl}/${userId}/add`, null, {
      params: {
        userId,
        productId,
        quantity
      },
      responseType: 'json'
    }).pipe(
      tap(cart => {
        console.log('🛒 Prodotto aggiunto al carrello:', cart);
        this.cartSubject.next(cart);
      }),
      catchError(error => this.handleError(error))
    );
  }

}
