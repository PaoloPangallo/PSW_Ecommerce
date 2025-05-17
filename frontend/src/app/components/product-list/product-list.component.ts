import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { combineLatest } from 'rxjs';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.services';

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
  isLoading: boolean = true;
  errorMessage: string | null = null;
  successMessage: string = '';

  page = 0;
  size = 8;
  totalPages = 0;

  private route = inject(ActivatedRoute);

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    combineLatest([this.route.paramMap, this.route.queryParams]).subscribe(
      ([params, queryParams]) => {
        this.category = params.get('categoryName') ? decodeURIComponent(params.get('categoryName')!) : null;
        this.searchTerm = queryParams['search'] || '';
        this.page = 0; // reset alla prima pagina su nuova categoria o ricerca
        this.loadProducts();
      }
    );
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.productService.getPagedProducts(this.page, this.size, this.category ?? undefined).subscribe({
      next: (pagedData) => {
        this.products = this.applySearchFilter(pagedData.content);
        this.totalPages = pagedData.totalPages;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('❌ Errore nel recupero dei prodotti:', error);
        this.errorMessage = "Errore nel caricamento dei prodotti. Riprova più tardi.";
        this.isLoading = false;
      }
    });
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
      this.authService.logout();
      this.successMessage = '';
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

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadProducts();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadProducts();
    }
  }
}


