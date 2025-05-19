import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ReviewDTO} from '../models/review.models';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private baseUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  // Recupera tutte le recensioni di un prodotto senza paginazione
  getReviewsByProductId(productId: number): Observable<ReviewDTO[]> {
    return this.http.get<ReviewDTO[]>(`${this.baseUrl}/product/${productId}/all`);
  }

  createReview(reviewData: any): Observable<ReviewDTO> {
    return this.http.post<ReviewDTO>(this.baseUrl, reviewData);
  }


  updateReview(reviewId: number, reviewData: any): Observable<ReviewDTO> {
    return this.http.put<ReviewDTO>(`${this.baseUrl}/${reviewId}`, reviewData);
  }

  // Metodo per cancellare una recensione


  uploadReviewImages(reviewId: number, images: File[]): Observable<void> {
    const formData = new FormData();
    images.forEach(file => formData.append('images', file));
    return this.http.post<void>(`${this.baseUrl}/${reviewId}/images`, formData);
  }

  reportReview(reviewId: number, userId: number, reason: string): Observable<void> {
    const params = new URLSearchParams();
    params.set('userId', userId.toString());
    params.set('reason', reason);

    return this.http.post<void>(`${this.baseUrl}/${reviewId}/report?${params.toString()}`, {});
  }

  getReportedReviews(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/reviews/reports/admin');
  }


  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`http://localhost:8080/api/reviews/${reviewId}`);
  }





}
