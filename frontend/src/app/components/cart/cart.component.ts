import { Component, OnInit } from '@angular/core';
import { CartService } from '../../services/cart.service';
import { CartDTO } from '../../models/cart.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  userId = 1; // Simulazione autenticazione
  cart: CartDTO | null = null;

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart(this.userId).subscribe({
      next: cart => {
        console.log("🔄 Carrello aggiornato nel componente:", cart);
        this.cart = cart;
      },
      error: err => console.error("Errore nel caricamento del carrello", err)
    });
  }

  increaseQuantity(productId: number, currentQuantity: number): void {
    const newQuantity = currentQuantity + 1;
    console.log("🆙 Aggiornamento quantità per productId:", productId, "a", newQuantity);
    this.cartService.updateItemQuantity(this.userId, productId, newQuantity).subscribe({
      next: () => this.loadCart(),
      error: err => console.error("Errore nell'aggiornamento della quantità", err)
    });
  }

  decreaseQuantity(productId: number, currentQuantity: number): void {
    const newQuantity = currentQuantity - 1;
    console.log("🔽 Aggiornamento quantità per productId:", productId, "a", newQuantity);
    if (newQuantity <= 0) {
      this.remove(productId);
    } else {
      this.cartService.updateItemQuantity(this.userId, productId, newQuantity).subscribe({
        next: () => this.loadCart(),
        error: err => console.error("Errore nell'aggiornamento della quantità", err)
      });
    }
  }

  remove(productId: number): void {
    console.log("Rimuovo item con productId:", productId);
    this.cartService.removeItem(this.userId, productId).subscribe({
      next: () => this.loadCart(),
      error: err => console.error("Errore nella rimozione", err)
    });
  }

  clear(): void {
    this.cartService.clearCart(this.userId).subscribe({
      next: () => this.loadCart(),
      error: err => console.error("Errore nello svuotamento del carrello", err)
    });
  }

  trackByProductId(index: number, item: any): number {
    return item.productId;
  }
}
