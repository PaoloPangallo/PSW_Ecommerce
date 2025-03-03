import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartDTO } from '../../models/cart.model';
import { AuthService } from '../../services/auth.services';
import { OrderService } from '../../services/order.service';  // IMPORTA OrderService

// Import Angular Material modules
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule, // Per utilizzare routerLink o navigate()
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: CartDTO | null = null;
  displayedColumns: string[] = ['productName', 'quantity', 'price', 'actions'];

  private cartService = inject(CartService);
  private authService = inject(AuthService);
  private orderService = inject(OrderService);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.snackBar.open('Nessun utente loggato.', 'Chiudi', { duration: 3000 });
      return;
    }
    this.loadCart(userId);
  }

  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe({
      next: cart => {
        this.cart = cart;
      },
      error: err => {
        console.error('Errore nel caricamento del carrello', err);
        this.snackBar.open(err.message, 'Chiudi', { duration: 5000 });
      }
    });
  }

  increaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.snackBar.open('Nessun utente loggato.', 'Chiudi', { duration: 3000 });
      return;
    }
    const newQuantity = currentQuantity + 1;
    this.cartService.updateItemQuantity(userId, productId, newQuantity).subscribe({
      next: () => this.loadCart(userId),
      error: err => {
        console.error("Errore nell'aggiornamento della quantità", err);
        this.snackBar.open(err.message, 'Chiudi', { duration: 5000 });
      }
    });
  }

  decreaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.snackBar.open('Nessun utente loggato.', 'Chiudi', { duration: 3000 });
      return;
    }
    const newQuantity = currentQuantity - 1;
    if (newQuantity <= 0) {
      this.remove(productId);
    } else {
      this.cartService.updateItemQuantity(userId, productId, newQuantity).subscribe({
        next: () => this.loadCart(userId),
        error: err => {
          console.error("Errore nell'aggiornamento della quantità", err);
          const errorMessage = err.error?.message || 'Si è verificato un errore imprevisto';
          this.snackBar.open(errorMessage, 'Chiudi', { duration: 5000 });
        }
      });
    }
  }

  remove(productId: number): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.snackBar.open('Nessun utente loggato.', 'Chiudi', { duration: 3000 });
      return;
    }
    this.cartService.removeItem(userId, productId).subscribe({
      next: () => this.loadCart(userId),
      error: err => {
        console.error('Errore nella rimozione', err);
        this.snackBar.open(err.message, 'Chiudi', { duration: 5000 });
      }
    });
  }

  clear(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.snackBar.open('Nessun utente loggato.', 'Chiudi', { duration: 3000 });
      return;
    }
    this.cartService.clearCart(userId).subscribe({
      next: () => this.loadCart(userId),
      error: err => {
        console.error('Errore nello svuotamento del carrello', err);
        this.snackBar.open(err.message, 'Chiudi', { duration: 5000 });
      }
    });
  }

  // Metodo per creare l'ordine direttamente
  createOrder(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.snackBar.open('Nessun utente loggato.', 'Chiudi', { duration: 3000 });
      return;
    }
    this.orderService.createOrder(userId).subscribe({
      next: (order) => {
        this.snackBar.open(`Ordine creato con successo. ID: ${order.id}`, 'Chiudi', { duration: 3000 });
        this.clear();
      },
      error: err => {
        console.error('Errore nella creazione dell\'ordine', err);
        this.snackBar.open(err.error || err.message, 'Chiudi', { duration: 5000 });
      }
    });
  }

  // Naviga al checkout multi-step
  goToCheckout(): void {
    this.router.navigate(['/checkout']);
  }

  trackByProductId(index: number, item: any): number {
    return item.productId;
  }

  getTotalPrice(): number {
    return this.cart?.items.reduce((total, item) => total + item.price * item.quantity, 0) || 0;
  }

}
