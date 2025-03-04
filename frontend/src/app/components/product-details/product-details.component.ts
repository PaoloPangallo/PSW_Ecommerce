import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
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
  wishlist: Wishlist | null = null;  // Memorizziamo la wishlist corrente
  private routeSubscription: Subscription | null = null;
  successMessage: string = ''; // Per mostrare un messaggio di conferma

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private wishlistService: WishlistService // Aggiunto WishlistService
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

    // Recupera la wishlist dell'utente loggato
    this.loadWishlist();
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

  loadWishlist(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error("Nessun utente loggato.");
      return;
    }

    this.wishlistService.getUserWishlist(userId).subscribe({
      next: (wishlist) => {
        this.wishlist = wishlist;
        console.log("Wishlist caricata:", wishlist);
      },
      error: (err) => {
        if (err.status === 404) {
          console.warn("Nessuna wishlist trovata, verrà creata quando necessario.");
          this.wishlist = null;
        } else {
          console.error("Errore caricando la wishlist:", err);
        }
      }
    });
  }

  addToWishlist(): void {
    if (!this.product) return;

    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error("Nessun utente loggato.");
      return;
    }

    // Se la wishlist non esiste, la creiamo prima di aggiungere il prodotto
    if (!this.wishlist) {
      this.wishlistService.createWishlist(userId).subscribe({
        next: (newWishlist) => {
          console.log("Wishlist creata:", newWishlist);
          this.wishlist = newWishlist;

          // Ora che la wishlist è creata, aggiungiamo il prodotto
          this.addProductToWishlist(this.wishlist.id, this.product!.id!);
        },
        error: (err) => {
          console.error("Errore creando la wishlist:", err);
        }
      });
    } else {
      this.addProductToWishlist(this.wishlist.id, this.product.id!);
    }
  }

  addProductToWishlist(wishlistId: number, productId: number): void {
    this.wishlistService.addProductToWishlist(wishlistId, productId).subscribe({
      next: (updatedWishlist) => {
        console.log("Prodotto aggiunto alla wishlist:", updatedWishlist);
        this.wishlist = updatedWishlist;
        this.successMessage = "Prodotto aggiunto alla wishlist!";
        setTimeout(() => this.successMessage = '', 3000); // Nasconde il messaggio dopo 3 secondi
      },
      error: (err) => {
        console.error("Errore aggiungendo il prodotto alla wishlist:", err);
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
