// src/app/components/product-details/product-details.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { CartDTO } from '../../models/cart.model';

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
    private cartService: CartService
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
    this.productService.getProductById(productId).subscribe(
      (data: Product) => {
        console.log("Prodotto ricevuto:", data);
        this.product = data;
        this.isLoading = false;
      },
      (error: any) => {
        console.error('Errore nel caricamento del prodotto:', error);
        this.isLoading = false;
      }
    );
  }

  // Metodo per aggiungere il prodotto al carrello usando addItem
  addToCart(): void {
    if (this.product) {
      const userId = 1; // In un'app reale, otterresti l'ID dell'utente loggato
      this.cartService.addItem(userId, this.product.id!, 1).subscribe({
        next: (data: CartDTO) => {
          console.log("Prodotto aggiunto al carrello:", data);
          // Puoi mostrare un messaggio di conferma o aggiornare l'interfaccia
        },
        error: (error: any) => {
          console.error("Errore nell'aggiunta al carrello:", error);
        }
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
