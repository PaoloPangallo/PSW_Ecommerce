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
    return Number((price * this.conversionRate).toFixed(2));
  }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl).pipe(
      map(products => products.map(product => ({
        ...product,
        price: this.convertToLira(product.price),
        discountedPrice: this.convertToLira(product.discountedPrice ?? product.price)
      })))
    );
  }

  getFeaturedProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/featured`).pipe(
      map(products => products.map(product => ({
        ...product,
        price: this.convertToLira(product.price),
        discountedPrice: this.convertToLira(product.discountedPrice ?? product.price)
      })))
    );
  }

  getProductsByCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}?category=${category}`).pipe(
      map(products => products.map(product => ({
        ...product,
        price: this.convertToLira(product.price),
        discountedPrice: this.convertToLira(product.discountedPrice ?? product.price)
      })))
    );
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`).pipe(
      map(product => ({
        ...product,
        price: this.convertToLira(product.price),
        discountedPrice: this.convertToLira(product.discountedPrice ?? product.price)
      }))
    );
  }

  getAlsoBoughtRecommendations(productId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/../recommendations/also-bought/${productId}`);
  }

  getRecommendations(userId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/../recommendations/for-user/${userId}`);
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
          price: this.convertToLira(product.price),
          discountedPrice: this.convertToLira(product.discountedPrice ?? product.price)
        }))
      }))
    );
  }

  uploadProductImage(productId: number, file: File): Observable<Product> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Product>(`${this.apiUrl}/${productId}/uploadImage`, formData);
  }

  updateProduct(product: Product): Observable<Product> {
    const productToSend = {
      id: product.id,
      name: product.name,
      description: product.description,
      stock: product.stock,
      imageUrl: product.imageUrl,
      featured: product.featured,
      discountPercentage: product.discountPercentage,
      price: Number((product.price / this.conversionRate).toFixed(2)) // euro
    };

    return this.http.put<Product>(`${this.apiUrl}/${product.id}`, productToSend);
  }
}
