import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth.services';
import {Observable} from 'rxjs';
import {CartDTO} from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class SavedService {
  private apiUrl = 'http://localhost:8080/api/saved'; // ✅ Chiamata diretta al backend

  constructor(private http: HttpClient, private auth: AuthService) {}

  getSavedItems(): Observable<any[]> {
    const userId = this.auth.getCurrentUserId();
    return this.http.get<any[]>(`${this.apiUrl}/${userId}`);
  }

  addToSaved(productId: number, quantity: number): Observable<any> {
    const userId = this.auth.getCurrentUserId();
    if (userId === null) throw new Error("User not logged in");

    return this.http.post<any>(`${this.apiUrl}/add`, null, {
      params: {
        userId: userId.toString(),
        productId: productId.toString(),
        quantity: quantity.toString()
      }
    });
  }


  removeSavedItem(productId: number): Observable<any> {
    const userId = this.auth.getCurrentUserId();
    if (userId === null) throw new Error("User not logged in");

    return this.http.delete(`${this.apiUrl}/remove`, {
      params: {
        userId: userId.toString(),
        productId: productId.toString()
      },
      responseType: 'text' as 'json'
    });
  }


  restoreToCart(userId: number, productId: number) {
    return this.http.post<CartDTO>(`http://localhost:8080/api/saved/restore`, null, {
      params: { userId, productId }
    });
  }






}
