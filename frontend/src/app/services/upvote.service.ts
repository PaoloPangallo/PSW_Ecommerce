import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UpvoteService {
  private baseUrl = 'http://localhost:8080/api/upvotes';

  constructor(private http: HttpClient) { }

  addUpvote(reviewId: number, userId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/review/${reviewId}/user/${userId}`, {});
  }

  removeUpvote(reviewId: number, userId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/review/${reviewId}/user/${userId}`);
  }
}
