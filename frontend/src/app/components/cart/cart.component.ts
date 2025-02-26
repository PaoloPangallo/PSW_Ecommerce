// src/app/components/cart/cart.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartDTO } from '../../models/cart.model';
import {AuthService} from '../../services/auth.services';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: CartDTO | null = null;
  private cartService = inject(CartService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    console.log("CartComponent: userId corrente:", userId);
    if (!userId) {
      console.error("Nessun utente loggato.");
      return;
    }
    this.loadCart(userId);
  }

  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe({
      next: cart => {
        console.log("🔄 Carrello aggiornato nel componente:", cart);
        this.cart = cart;
      },
      error: err => console.error("Errore nel caricamento del carrello", err)
    });
  }

  increaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error("Nessun utente loggato.");
      return;
    }
    const newQuantity = currentQuantity + 1;
    console.log("🆙 Aggiornamento quantità per productId:", productId, "a", newQuantity);
    this.cartService.updateItemQuantity(userId, productId, newQuantity).subscribe({
      next: () => this.loadCart(userId),
      error: err => console.error("Errore nell'aggiornamento della quantità", err)
    });
  }

  decreaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error("Nessun utente loggato.");
      return;
    }
    const newQuantity = currentQuantity - 1;
    console.log("🔽 Aggiornamento quantità per productId:", productId, "a", newQuantity);
    if (newQuantity <= 0) {
      this.remove(productId);
    } else {
      this.cartService.updateItemQuantity(userId, productId, newQuantity).subscribe({
        next: () => this.loadCart(userId),
        error: err => console.error("Errore nell'aggiornamento della quantità", err)
      });
    }
  }

  remove(productId: number): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error("Nessun utente loggato.");
      return;
    }
    console.log("Rimuovo item con productId:", productId);
    this.cartService.removeItem(userId, productId).subscribe({
      next: () => this.loadCart(userId),
      error: err => console.error("Errore nella rimozione", err)
    });
  }

  clear(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error("Nessun utente loggato.");
      return;
    }
    console.log("Svuoto il carrello");
    this.cartService.clearCart(userId).subscribe({
      next: () => this.loadCart(userId),
      error: err => console.error("Errore nello svuotamento del carrello", err)
    });
  }

  trackByProductId(index: number, item: any): number {
    return item.productId;
  }
}
