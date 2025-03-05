import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  featuredProducts: Product[] = [];
  allProducts: Product[] = [];

  private productService = inject(ProductService);

  ngOnInit(): void {
    this.loadFeaturedProducts();
    this.loadAllProducts();
  }

  loadFeaturedProducts(): void {
    this.productService.getFeaturedProducts().subscribe({
      next: (products) => {
        this.featuredProducts = products;
        console.log("Prodotti in evidenza ricevuti:", products);
      },
      error: (err) => {
        console.error('Errore nel caricamento dei prodotti in evidenza:', err);
      }
    });
  }

  loadAllProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (products) => {
        this.allProducts = products;
        console.log("Tutti i prodotti ricevuti:", products);
      },
      error: (err) => {
        console.error('Errore nel caricamento di tutti i prodotti:', err);
      }
    });
  }
}
