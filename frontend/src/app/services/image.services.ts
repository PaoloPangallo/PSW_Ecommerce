import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interfaccia che corrisponde alla risposta JSON del backend
export interface ImageUploadResponse {
  fileName: string;
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private baseUrl = 'http://localhost:8080/api/images';

  constructor(private http: HttpClient) { }

  // Chiama il tuo endpoint /api/images/generate/{productId} passando il prompt
  generateImage(productId: number, prompt: string): Observable<ImageUploadResponse> {
    return this.http.post<ImageUploadResponse>(`${this.baseUrl}/generate/${productId}`, { prompt });
  }
}
