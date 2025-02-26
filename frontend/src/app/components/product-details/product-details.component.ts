// src/app/components/product-details/product-details.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import {AuthService} from '../../services/auth.services';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit, OnDestroy {
  product: Product | null = null;
  isLoading: boolean = false;
  private routeSubscription: Subscription | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.routeSubscription = this.route.paramMap.subscribe(params => {
      const idStr = params.get('id');
      const productId = idStr ? Number(idStr) : 0;
      console.log("Recupero prodotto ID:", productId);
      if (productId) {
        this.loadProduct(productId);
      } else {
        console.warn("ID prodotto non valido");
      }
    });
  }

  loadProduct(productId: number): void {
    this.isLoading = true;
    this.productService.getProductById(productId).subscribe({
      next: (data: Product) => {
        console.log("Prodotto ricevuto:", data);
        this.product = data;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Errore nel caricamento del prodotto:', error);
        this.isLoading = false;
      }
    });
  }

  addToCart(): void {
    if (this.product) {
      const userId = this.authService.getCurrentUserId();
      if (!userId) {
        console.error("Nessun utente loggato.");
        return;
      }
      console.log("Aggiungo prodotto con ID:", this.product.id);
      this.cartService.updateItemQuantity(userId, this.product.id!, 1).subscribe({
        next: () => console.log("Prodotto aggiunto al carrello."),
        error: (error: any) => console.error("Errore nell'aggiunta al carrello:", error)
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.routeSubscription?.unsubscribe();
  }
}
