import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Wishlist } from '../models/wishlist.model';

@Injectable({
  providedIn: 'root',
})
export class WishlistService {
  private apiUrl = 'http://localhost:8080/api/wishlists'; // ✅ Corretto il baseUrl

  constructor(private http: HttpClient) {}

  // 1. Creare una nuova wishlist per un utente
  createWishlist(userId: number): Observable<Wishlist> {
    return this.http.post<Wishlist>(`${this.apiUrl}/user/${userId}`, {}); // ✅ Corretto
  }

  // 2. Ottenere la wishlist di un utente
  getUserWishlist(userId: number): Observable<Wishlist> {
    return this.http.get<Wishlist>(`${this.apiUrl}/user/${userId}`); // ✅ Corretto
  }

  // 3. Aggiungere un prodotto alla wishlist
  addProductToWishlist(wishlistId: number, productId: number): Observable<Wishlist> {
    return this.http.post<Wishlist>(`${this.apiUrl}/${wishlistId}/products/${productId}`, {}); // ✅ Corretto
  }

  // 4. Rimuovere un prodotto dalla wishlist
  removeProductFromWishlist(wishlistId: number, productId: number): Observable<Wishlist> {
    return this.http.delete<Wishlist>(`${this.apiUrl}/${wishlistId}/products/${productId}`); // ✅ Corretto
  }

  // 5. Eliminare una wishlist
  deleteWishlist(wishlistId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${wishlistId}`); // ✅ Corretto
  }
}
