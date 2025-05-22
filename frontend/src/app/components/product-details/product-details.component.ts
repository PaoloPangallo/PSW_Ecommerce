import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { CommonModule, Location } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.services';
import { WishlistService } from '../../services/wishlist.service';
import { Wishlist } from '../../models/wishlist.model';
import { ReviewListComponent } from '../review/review-list.component';
import { ReviewFormComponent } from '../review-form/review-form.component';
import { ReviewService } from '../../services/review-list.services';
import { ReviewDTO } from '../../models/review.models';
import { CouponService } from '../../services/coupon.service';
import { Coupon } from '../../models/coupon.model';
import { LirePipe } from '../../services/lire.pipe';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, RouterModule, ReviewListComponent, ReviewFormComponent, LirePipe],
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.scss']
})
export class ProductDetailsComponent implements OnInit, OnDestroy {
  product: Product | null = null;
  alsoBoughtProducts: Product[] = []; // 🔥 Nuova proprietà
  isLoading: boolean = false;
  wishlist: Wishlist | null = null;
  successMessage: string = '';
  errorMessage: string = '';
  coupons: Coupon[] = [];
  private routeSubscription: Subscription | null = null;

  selectedFile: File | null = null;
  reviews: ReviewDTO[] = [];
  couponMessage: string = '';
  userHasReviewed: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private productService: ProductService,
    private cartService: CartService,
    protected authService: AuthService,
    private wishlistService: WishlistService,
    private reviewService: ReviewService,
    private couponService: CouponService
  ) {}

  ngOnInit(): void {
    this.routeSubscription = this.route.paramMap.subscribe(params => {
      const idStr = params.get('id');
      const productId = idStr ? Number(idStr) : 0;

      if (productId) {
        this.loadProduct(productId);
        this.loadReviewsForProduct(productId);
        this.loadApplicableCoupons(productId);
        this.loadAlsoBoughtRecommendations(productId); // 🔥
      } else {
        this.errorMessage = "ID prodotto non valido.";
        this.redirectToHome();
      }
    });

    this.loadWishlist();
  }

  loadAlsoBoughtRecommendations(productId: number): void {
    this.productService.getAlsoBoughtRecommendations(productId).subscribe({
      next: (products) => {
        this.alsoBoughtProducts = products;
      },
      error: (err) => {
        console.error('Errore nel caricamento dei co-acquisti:', err);
      }
    });
  }


  // ===========================
  // = UPLOAD IMMAGINE LOGICA =
  // ===========================
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  uploadImage(): void {
    if (this.product && this.selectedFile) {
      this.productService.uploadProductImage(this.product.id!, this.selectedFile)
        .subscribe({
          next: (updatedProduct: Product) => {
            // Aggiorniamo il product locale con l'URL appena generato
            this.product = updatedProduct;
            this.successMessage = "Immagine caricata con successo!";
            // Reset del file selezionato (opzionale)
            this.selectedFile = null;
          },
          error: (err) => {
            console.error("Errore durante l'upload dell'immagine:", err);
            this.errorMessage = "Errore nell'upload dell'immagine.";
          }
        });
    }
  }

  // ===========================
  // = METODI DI GESTIONE PRODOTTO, COUPON, REVIEW, ECC. =
  // ===========================
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

  loadReviewsForProduct(productId: number): void {
    this.reviewService.getReviewsByProductId(productId).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
        const currentUserId = this.authService.getCurrentUserId();
        this.userHasReviewed = reviews.some(review => review.userId === currentUserId);
      },
      error: (err) => {
        console.error('Errore nel caricamento delle recensioni', err);
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

  loadApplicableCoupons(productId: number): void {
    this.couponService.getCouponsForProduct(productId).subscribe({
      next: (coupons: Coupon[]) => {
        this.coupons = coupons;
      },
      error: (err: any) => {
        console.error("Errore nel caricamento dei coupon applicabili:", err);
      }
    });
  }
  goBack(): void {
    this.location.back();
  }

  redirectToHome(): void {
    setTimeout(() => this.router.navigate(['/']), 2000);
  }

  ngOnDestroy(): void {
    this.routeSubscription?.unsubscribe();
  }

  copyCouponCode(code: string): void {
    navigator.clipboard.writeText(code).then(() => {
      this.couponMessage = `✅ Coupon "${code}" copiato negli appunti! Incollalo nel carrello.`;
      setTimeout(() => this.couponMessage = '', 4000);
    }).catch(err => {
      console.error("Errore nella copia del coupon:", err);
      this.couponMessage = "❌ Errore nella copia del coupon.";
      setTimeout(() => this.couponMessage = '', 4000);
    });
  }
}
