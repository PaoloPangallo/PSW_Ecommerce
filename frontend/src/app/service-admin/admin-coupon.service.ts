import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Coupon, CouponCreationDTO} from '../models/coupon.model';

@Injectable({
  providedIn: 'root'
})
export class AdminCouponService {
  private apiUrl = 'http://localhost:8080/api/coupons';

  constructor(private http: HttpClient) { }

  // Recupera tutti i coupon
  getCoupons(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(this.apiUrl);
  }

  // Crea un nuovo coupon utilizzando il CouponCreationDTO
  createCoupon(dto: CouponCreationDTO): Observable<Coupon> {
    return this.http.post<Coupon>(`${this.apiUrl}/create`, dto);
  }

  getAllCoupons(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(this.apiUrl);
  }

}
