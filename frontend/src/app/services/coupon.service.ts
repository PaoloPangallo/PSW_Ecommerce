import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Coupon } from '../models/coupon.model';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private baseUrl = 'http://localhost:8080/api/coupons';

  constructor(private http: HttpClient) {}

  // Creazione di un coupon
  createCoupon(coupon: Coupon): Observable<Coupon> {
    return this.http.post<Coupon>(`${this.baseUrl}/create`, coupon);
  }

  // Recupera un coupon specifico tramite il codice
  getCouponByCode(code: string): Observable<Coupon> {
    return this.http.get<Coupon>(`${this.baseUrl}/${code}`);
  }

  // Validazione di un coupon in base al codice e al valore dell'ordine
  validateCoupon(code: string, orderValue: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/validate/${code}/${orderValue}`);
  }

  // Recupera la lista di tutti i coupon
  getAllCoupons(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(this.baseUrl);
  }

  // Metodo aggiuntivo per recuperare i coupon applicabili a un prodotto
  getCouponsForProduct(productId: number): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(`${this.baseUrl}/applicable/${productId}`);
  }
}
