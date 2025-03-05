import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ProductService } from './services/product.service';
import { Product } from './models/product.model';
import {ShowcaseComponent} from './components/showcase/showcase.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, ShowcaseComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'E-Commerce';
  products: Product[] = [];
  filteredProducts: Product[] = []; // Per i prodotti normali
  featuredProducts: Product[] = []; // ✅ Nuovo array per la vetrina

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadFeaturedProducts(); // ✅ Carica anche i prodotti in evidenza
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (response) => {
        this.products = response;
        this.filteredProducts = response; // Inizialmente mostra tutti
      },
      error: (error) => {
        console.error('Errore nel caricamento dei prodotti:', error);
      }
    });
  }

  loadFeaturedProducts(): void {
    this.productService.getFeaturedProducts().subscribe({
      next: (response) => {
        this.featuredProducts = response;
      },
      error: (error) => {
        console.error('Errore nel caricamento dei prodotti in evidenza:', error);
      }
    });
  }
}
