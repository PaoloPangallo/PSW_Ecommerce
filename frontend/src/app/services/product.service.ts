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

  createProduct(product: Product): Observable<Product> {
    // Se il prezzo è già in euro, lo inviamo direttamente al backend
    const productInEuro = { ...product, price: Number((product.price / this.conversionRate).toFixed(2)) };
    return this.http.post<Product>(this.apiUrl, productInEuro);
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    const productInEuro = { ...product, price: Number((product.price / this.conversionRate).toFixed(2)) };
    return this.http.put<Product>(`${this.apiUrl}/${id}`, productInEuro);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }


  uploadProductImage(productId: number, file: File): Observable<Product> {
    const formData = new FormData();
    formData.append('file', file);

    // POST /api/products/{id}/uploadImage
    return this.http.post<Product>(`${this.apiUrl}/${productId}/uploadImage`, formData);
  }

}
