import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartDTO } from '../../models/cart.model';
import { AuthService } from '../../services/auth.services';
import { OrderService } from '../../services/order.service';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { LirePipe } from '../../services/lire.pipe';
import { FormsModule } from '@angular/forms';
import {SavedService} from '../../services/saved-for-later.service';
import {MatInput} from '@angular/material/input';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    LirePipe,
    FormsModule,
    MatInput
  ],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: CartDTO | null = null;
  displayedColumns: string[] = ['productName', 'quantity', 'price', 'total', 'coupon', 'actions'];
  // Oggetto per tenere traccia del coupon inserito per ogni item (chiave = cartItem ID)
  couponCodes: { [key: number]: string } = {};

  private cartService = inject(CartService);
  private authService = inject(AuthService);
  private orderService = inject(OrderService);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);
  private savedService = inject(SavedService);


  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.showSnack('🔒 Devi effettuare il login per accedere al carrello.', 3000);
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url }
      });
      return;
    }

    this.loadCart(userId);
    this.loadSaved(); // ✅ mancava questa riga!
  }



  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe({
      next: (cart) => {
        this.cart = cart;
        console.log('Carrello caricato:', cart);
      },
      error: (err) => {
        console.error('Errore nel caricamento del carrello:', err);
        this.showSnack(err.message, 5000);
      }
    });
  }

  increaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;
    const newQuantity = currentQuantity + 1;
    this.updateItemQuantity(userId, productId, newQuantity);
  }

  decreaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;
    const newQuantity = currentQuantity - 1;
    if (newQuantity <= 0) {
      this.remove(productId);
    } else {
      this.updateItemQuantity(userId, productId, newQuantity);
    }
  }

  remove(productId: number): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;
    this.cartService.removeItem(userId, productId).subscribe({
      next: () => {
        this.loadCart(userId);
      },
      error: (err) => {
        console.error('Errore nella rimozione:', err);
        this.showSnack(err.message, 5000);
      }
    });
  }

  clear(): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;
    this.cartService.clearCart(userId).subscribe({
      next: () => {
        this.loadCart(userId);
      },
      error: (err) => {
        console.error('Errore nello svuotamento del carrello:', err);
        this.showSnack(err.message, 5000);
      }
    });
  }

  goToCheckout(): void {
    this.router.navigate(['/checkout']);
  }

  getTotalPrice(): number {
    return this.cart?.items.reduce((total, item) =>
      total + this.getDiscountedPrice(item.price, item.discountPercentage) * item.quantity, 0
    ) || 0;
  }


  private updateItemQuantity(userId: number, productId: number, newQuantity: number): void {
    this.cartService.updateItemQuantity(userId, productId, newQuantity).subscribe({
      next: () => {
        this.loadCart(userId);
      },
      error: (err) => {
        console.error("Errore nell'aggiornamento della quantità:", err);
        const errorMessage = err.error?.message || 'Si è verificato un errore imprevisto';
        this.showSnack(errorMessage, 5000);
      }
    });
  }

  // Metodo per applicare il coupon a un item
  applyCoupon(cartItemId: number): void {
    const couponCode = this.couponCodes[cartItemId];
    if (!couponCode) {
      this.showSnack("⚠️ Inserisci un codice coupon", 3000);
      return;
    }
    this.cartService.applyCouponToItem(cartItemId, couponCode).subscribe({
      next: (cart) => {
        this.showSnack("🎉 Coupon applicato con successo!", 3000);
        // Ricarica il carrello per visualizzare i prezzi aggiornati
        const userId = this.authService.getCurrentUserId();
        if (userId) {
          this.loadCart(userId);
        }
      },
      error: (err) => {
        console.error("Errore nell'applicazione del coupon:", err);
        this.showSnack(err.error?.message || "Errore nell'applicazione del coupon", 5000);
      }
    });
  }

  private checkUserLoggedIn(): number | null {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.showSnack('🔒 Devi effettuare il login per continuare.', 3000);
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url }
      });
      return null;
    }
    return userId;
  }


  private showSnack(message: string, duration: number = 3000): void {
    this.snackBar.open(message, 'OK', { duration, horizontalPosition: 'right', verticalPosition: 'top' });
  }

  saveForLater(item: any): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;

    this.cartService.removeItem(userId, item.productId).subscribe({
      next: () => {
        this.savedService.addToSaved(item.productId, item.quantity).subscribe({
          next: () => {
            this.showSnack('💾 Prodotto salvato per dopo!', 2000);
            this.loadCart(userId);
            this.loadSaved();
          },
          error: (err) => {
            console.error('Errore nel salvataggio per dopo:', err);
            this.showSnack('❌ Errore nel salvataggio.', 3000);
          }
        });
      },
      error: (err) => {
        console.error('Errore nella rimozione dal carrello:', err);
        this.showSnack('❌ Errore nella rimozione.', 3000);
      }
    });
  }



  savedItems: any[] = [];

  loadSaved(): void {
    this.savedService.getSavedItems().subscribe({
      next: (items) => this.savedItems = items
    });
  }

  moveToCart(item: any): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.cartService.addToCart(userId, item.product.id, item.quantity).subscribe({
      next: () => {
        this.savedService.removeSavedItem(item.product.id).subscribe({
          next: () => {
            this.loadCart(userId);
            this.loadSaved();
            this.showSnack('✅ Prodotto spostato nel carrello', 2000);
          }
        });
      }
    });
  }

  removeFromSaved(item: any): void {
    this.savedService.removeSavedItem(item.product.id).subscribe(() => {
      this.loadSaved();
      this.showSnack('🗑️ Rimosso dai salvati', 2000);
    });
  }

  getDiscountedPrice(price: number, discountPercentage: number | null | undefined): number {
    if (!discountPercentage || discountPercentage <= 0) return price;
    return price - (price * discountPercentage) / 100;
  }





}
