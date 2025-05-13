import { Component, OnInit } from '@angular/core';
import { Product } from '../../../models/product.model';
import { ProductService } from '../../../services/product.service';
import { AuthService } from '../../../services/auth.services';
import { RouterLink } from '@angular/router';
import { CommonModule, NgIf, NgForOf } from '@angular/common';

@Component({
  selector: 'app-recommended-products',
  templateUrl: './recommended-products.component.html',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    NgIf,
    NgForOf
  ]
})
export class RecommendedProductsComponent implements OnInit {
  recommendedProducts: Product[] = [];

  constructor(
    private productService: ProductService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.productService.getRecommendations(userId).subscribe({
        next: (products) => (this.recommendedProducts = products),
        error: (err) => console.error('Errore nel caricamento', err),
      });
    }
  }
}
