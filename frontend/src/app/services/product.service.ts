import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/products';
  private conversionRate = 1936.27; // 1€ = 1936.27 lire

  constructor(private http: HttpClient) {}

  private convertToLira(price: number): number {
    return Number((price * this.conversionRate).toFixed(2)); // Mantiene 2 decimali
  }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl).pipe(
      map(products => products.map(product => ({
        ...product,
        price: this.convertToLira(product.price)
      })))
    );
  }

  getFeaturedProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/featured`).pipe(
      map(products => products.map(product => ({
        ...product,
        price: this.convertToLira(product.price)
      })))
    );
  }

  getProductsByCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}?category=${category}`).pipe(
      map(products => products.map(product => ({
        ...product,
        price: this.convertToLira(product.price)
      })))
    );
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`).pipe(
      map(product => ({
        ...product,
        price: this.convertToLira(product.price)
      }))
    );
  }


  uploadProductImage(productId: number, file: File): Observable<Product> {
    const formData = new FormData();
    formData.append('file', file);

    // POST /api/products/{id}/uploadImage
    return this.http.post<Product>(`${this.apiUrl}/${productId}/uploadImage`, formData);
  }

  getAlsoBoughtRecommendations(productId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`http://localhost:8080/api/recommendations/also-bought/${productId}`);
  }

  getRecommendations(userId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`http://localhost:8080/api/recommendations/for-user/${userId}`);
  }

  searchProducts(query: string): Observable<Product[]> {
    return this.http.get<Product[]>(`http://localhost:8080/api/search`, {
      params: { query }
    });
  }

  getPagedProducts(page: number, size: number, category?: string): Observable<any> {
    let url = `${this.apiUrl}/paged?page=${page}&size=${size}`;
    if (category) {
      url += `&category=${category}`;
    }

    return this.http.get<any>(url).pipe(
      map(response => ({
        ...response,
        content: response.content.map((product: Product) => ({
          ...product,
          price: this.convertToLira(product.price)
        }))
      }))
    );
  }




}
