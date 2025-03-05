import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-showcase',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './showcase.component.html',
  styleUrls: ['./showcase.component.css']
})
export class ShowcaseComponent implements OnInit {
  featuredProducts: Product[] = [];
  private productService = inject(ProductService);

  ngOnInit(): void {
    this.loadFeaturedProducts();
  }

  loadFeaturedProducts(): void {
    this.productService.getFeaturedProducts().subscribe({
      next: (products) => {
        this.featuredProducts = products;
      },
      error: (err) => {
        console.error('Errore nel caricamento dei prodotti in evidenza:', err);
      }
    });
  }

  addToCart(product: Product): void {
    console.log('Aggiunto al carrello:', product.name);
    // Qui puoi usare un servizio del carrello (CartService)
    // per aggiungere il prodotto
  }

}
