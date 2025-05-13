// home-recommendations.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { RouterModule } from '@angular/router';
import {AuthService} from '../../services/auth.services';

@Component({
  selector: 'app-home-recommendations',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home-recommendations.component.html',
  styleUrls: ['./home-recommendations.component.css']
})
export class HomeRecommendationsComponent implements OnInit {
  recommendedProducts: Product[] = [];

  constructor(private productService: ProductService, public authService: AuthService) {}

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.productService.getRecommendations(userId).subscribe({
        next: (products) => {
          this.recommendedProducts = products;
        },
        error: (err) => {
          console.error('Errore nel caricamento dei consigli:', err);
        }
      });
    }
  }
}
