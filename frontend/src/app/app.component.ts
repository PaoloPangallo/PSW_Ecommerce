import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ProductService } from './services/product.service';
import { Product } from './models/product.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'E-Commerce';
  products: Product[] = [];
  filteredProducts: Product[] = []; // Array per i prodotti filtrati

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (response) => {
        this.products = response;
        // Inizialmente, mostra tutti i prodotti
        this.filteredProducts = response;
      },
      error: (error) => {
        console.error('Errore nel caricamento dei prodotti:', error);
      }
    });
  }

  viewProductDetails(productId: number | undefined): void {
    console.log(`Visualizzazione dettagli per il prodotto con ID: ${productId}`);
  }

  // Metodo per gestire la ricerca dalla Navbar
  onSearch(term: string): void {
    console.log('Termine di ricerca ricevuto:', term);
    if (!term) {
      this.filteredProducts = this.products;
    } else {
      this.filteredProducts = this.products.filter(p =>
        p.name.toLowerCase().includes(term.toLowerCase())
      );
    }
  }
}
