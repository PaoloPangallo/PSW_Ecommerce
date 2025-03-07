import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import { ImageService, ImageUploadResponse } from '../../../services/image.services';
import { ProductService } from '../../../services/product.service'; // <-- Import del ProductService
import { Product } from '../../../models/product.model';

@Component({
  selector: 'app-product-image-generator',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './product-image-generator.component.html',
  styleUrls: ['./product-image-generator.component.css']
})
export class ProductImageGeneratorComponent implements OnInit {
  productId!: number;
  productName: string = '';      // <-- Variabile per memorizzare il nome
  prompt: string = '';
  generatedImageUrl?: string;
  loading = false;
  errorMsg?: string;

  constructor(
    private route: ActivatedRoute,
    private location: Location,
    private imageService: ImageService,
    private productService: ProductService  // <-- Iniettiamo il ProductService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('productId');
      if (idParam) {
        this.productId = Number(idParam);

        // Richiama il servizio per ottenere il prodotto
        this.productService.getProductById(this.productId).subscribe({
          next: (product: Product) => {
            this.productName = product.name;
            // Inizializza un prompt di default
            this.prompt = `Genera un'immagine coerente con ${this.productName}`;
          },
          error: (err) => {
            this.errorMsg = 'Errore nel recupero del prodotto.';
            console.error(err);
          }
        });
      } else {
        this.errorMsg = 'ID prodotto non valido.';
      }
    });
  }

  onGenerate(): void {
    if (!this.prompt.trim()) {
      this.errorMsg = 'Il prompt non può essere vuoto.';
      return;
    }
    this.loading = true;
    this.errorMsg = undefined;

    this.imageService.generateImage(this.productId, this.prompt)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (res: ImageUploadResponse) => {
          this.generatedImageUrl = res.url;
        },
        error: (err) => {
          this.errorMsg = 'Errore durante la generazione dell\'immagine';
          console.error(err);
        }
      });
  }

  goBack(): void {
    this.location.back();
  }
}
