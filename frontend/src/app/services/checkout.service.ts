import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Transaction {
  paymentMethod: string;  // <-- Campo direttamente in Transaction
  amount: number;
  status: string;
}

export interface Shipping {
  address: string;
  city: string;
  zipCode: string;
  country: string;
  shippingMethod: string;
}

export interface CheckoutRequest {
  transaction: Transaction;
  shipping: Shipping;
}

export interface CheckoutResponse {
  orderId: number | null;
  status: string | null;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {
  private apiUrl = 'http://localhost:8080/checkout';

  constructor(private http: HttpClient) {}

  processCheckout(userId: number, checkoutRequest: CheckoutRequest, token: string | null): Observable<CheckoutResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<CheckoutResponse>(`${this.apiUrl}/${userId}`, checkoutRequest, { headers })
      .pipe(
        catchError(error => {
          console.error('Errore durante il checkout:', error);
          return throwError(() => new Error('Errore durante il checkout, riprova più tardi.'));
        })
      );
  }
}
