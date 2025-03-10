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
  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${reviewId}`);
  }

}
