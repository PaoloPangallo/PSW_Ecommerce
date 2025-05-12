import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { combineLatest } from 'rxjs';
import {CartService} from '../../services/cart.service';
import {AuthService} from '../../services/auth.services';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  category: string | null = null;
  searchTerm: string = '';
  isLoading: boolean = true; // Nuovo stato di caricamento
  errorMessage: string | null = null; // Nuovo stato per gestire gli errori

  private route = inject(ActivatedRoute);

  constructor(
    private productService: ProductService,
  private cartService: CartService,       // <-- aggiungi
  private authService: AuthService) {}

  ngOnInit(): void {
    combineLatest([this.route.paramMap, this.route.queryParams]).subscribe(
      ([params, queryParams]) => {
        this.category = params.get('categoryName') ? decodeURIComponent(params.get('categoryName')!) : null;
        this.searchTerm = queryParams['search'] || '';

        console.log("✅ Categoria ricevuta:", this.category);
        console.log("✅ Termine di ricerca ricevuto:", this.searchTerm);

        this.loadProducts();
      }
    );
  }

  loadProducts(): void {
    this.isLoading = true; // Attiviamo il loading state
    this.errorMessage = null; // Reset errori

    const productsObservable = this.category
      ? this.productService.getProductsByCategory(this.category)
      : this.productService.getAllProducts();

    productsObservable.subscribe(
      (data) => {
        this.products = this.applySearchFilter(data);
        this.isLoading = false; // Disattiviamo il loading
      },
      (error) => {
        console.error('❌ Errore nel recupero dei prodotti:', error);
        this.errorMessage = "Errore nel caricamento dei prodotti. Riprova più tardi.";
        this.isLoading = false;
      }
    );
  }

  private applySearchFilter(data: Product[]): Product[] {
    return this.searchTerm
      ? data.filter(product => product.name.toLowerCase().includes(this.searchTerm.toLowerCase()))
      : data;
  }




  addToCart(product: Product): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      alert('🔒 Devi effettuare il login per aggiungere un prodotto al carrello.');
      this.authService.logout(); // opzionale: rimuovi token se scaduto
      this.successMessage = ''; // reset messaggio
      location.href = `/login?returnUrl=${encodeURIComponent(location.pathname)}`;
      return;
    }

    this.cartService.updateItemQuantity(userId, product.id!, 1).subscribe({
      next: () => {
        this.successMessage = "🛒 Prodotto aggiunto al carrello!";
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (error: any) => {
        console.error("Errore nell'aggiunta al carrello:", error);
        this.successMessage = "❌ Errore durante l'aggiunta al carrello.";
        setTimeout(() => this.successMessage = '', 4000);
      }
    });
  }



  successMessage: string = '';


}
