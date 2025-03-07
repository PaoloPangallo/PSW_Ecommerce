import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { Location } from '@angular/common'; // Importa Location

import { AuthService } from '../../services/auth.services';
import { WishlistService } from '../../services/wishlist.service';
import { Wishlist } from '../../models/wishlist.model';

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
  wishlist: Wishlist | null = null;
  private routeSubscription: Subscription | null = null;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location, // Iniezione di Location

    private productService: ProductService,
    private cartService: CartService,
    protected authService: AuthService,
    private wishlistService: WishlistService
  ) {}

  ngOnInit(): void {
    this.routeSubscription = this.route.paramMap.subscribe(params => {
      const idStr = params.get('id');
      const productId = idStr ? Number(idStr) : 0;

      if (productId) {
        this.loadProduct(productId);
      } else {
        this.errorMessage = "ID prodotto non valido.";
        this.redirectToHome();
      }
    });

    this.loadWishlist();
  }

  loadProduct(productId: number): void {
    this.isLoading = true;
    this.productService.getProductById(productId).subscribe({
      next: (data: Product) => {
        this.product = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = "Errore nel caricamento del prodotto.";
        this.redirectToHome();
      }
    });
  }

  loadWishlist(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.wishlistService.getUserWishlist(userId).subscribe({
      next: (wishlist) => this.wishlist = wishlist,
      error: (err) => {
        if (err.status === 404) this.wishlist = null;
        else console.error("Errore caricando la wishlist:", err);
      }
    });
  }

  addToWishlist(): void {
    if (!this.product) return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    if (!this.wishlist) {
      this.wishlistService.createWishlist(userId).subscribe({
        next: (newWishlist) => {
          this.wishlist = newWishlist;
          this.addProductToWishlist(this.wishlist.id, this.product!.id!);
        },
        error: (err) => console.error("Errore creando la wishlist:", err)
      });
    } else {
      this.addProductToWishlist(this.wishlist.id, this.product.id!);
    }
  }

  addProductToWishlist(wishlistId: number, productId: number): void {
    this.wishlistService.addProductToWishlist(wishlistId, productId).subscribe({
      next: (updatedWishlist) => {
        this.wishlist = updatedWishlist;
        this.successMessage = "✅ Prodotto aggiunto alla wishlist!";
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => console.error("Errore aggiungendo il prodotto alla wishlist:", err)
    });
  }

  addToCart(): void {
    if (!this.product) return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.cartService.updateItemQuantity(userId, this.product.id!, 1).subscribe({
      next: () => {
        this.successMessage = "🛒 Prodotto aggiunto al carrello!";
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (error: any) => console.error("Errore nell'aggiunta al carrello:", error)
    });
  }

  goBack(): void {
    this.location.back(); // Torna alla pagina precedente
  }

  redirectToHome(): void {
    setTimeout(() => this.router.navigate(['/']), 2000);
  }

  ngOnDestroy(): void {
    this.routeSubscription?.unsubscribe();
  }
}
