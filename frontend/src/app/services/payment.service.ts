// src/app/services/payment.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaymentRequestDTO, PaymentResponse } from '../models/payment.models';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private baseUrl = 'http://localhost:8080/api/payments';

  constructor(private http: HttpClient) {}

  // Crea un nuovo pagamento
  createPayment(request: PaymentRequestDTO): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(this.baseUrl, request);
  }

  // Recupera tutti i pagamenti
  getAllPayments(): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(this.baseUrl);
  }

  // Recupera un pagamento per ID
  getPaymentById(id: number): Observable<PaymentResponse> {
    return this.http.get<PaymentResponse>(`${this.baseUrl}/${id}`);
  }

  // Aggiorna lo stato di un pagamento
  updatePaymentStatus(id: number, status: string): Observable<PaymentResponse> {
    return this.http.patch<PaymentResponse>(`${this.baseUrl}/${id}/status?status=${status}`, {});
  }

  // Elimina un pagamento
  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
