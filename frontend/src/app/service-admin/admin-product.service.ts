import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class AdminProductService {
  // URL di base corrispondente al controller backend (che gestisce sia operazioni di lettura sia admin)
  private baseUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  // Recupera tutti i prodotti, con eventuale filtro per categoria
  getProducts(category?: string): Observable<Product[]> {
    if (category) {
      return this.http.get<Product[]>(`${this.baseUrl}?category=${category}`);
    }
    return this.http.get<Product[]>(this.baseUrl);
  }

  // Recupera un singolo prodotto tramite ID
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  // Operazione Admin: Crea un nuovo prodotto
  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, product);
  }

  // Operazione Admin: Aggiorna un prodotto esistente
  updateProduct(id: number | undefined, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, product);
  }

  // Operazione Admin: Elimina un prodotto
  deleteProduct(id: number | undefined): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Operazione Admin: Carica (upload) l'immagine di un prodotto
  uploadProductImage(id: number, file: File): Observable<Product> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Product>(`${this.baseUrl}/${id}/uploadImage`, formData);
  }

  // (Opzionale) Recupera i prodotti in evidenza
  getFeaturedProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/featured`);
  }
}
