// src/app/components/cart/cart.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartDTO } from '../../models/cart.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div *ngIf="cart && cart.items.length > 0; else emptyCart">
      <h2>Il tuo Carrello</h2>
      <ul>
        <li *ngFor="let item of cart.items">
          Prodotto ID: {{ item.productId }} – Nome: {{ item.productName }} – Quantità: {{ item.quantity }}
          <button (click)="remove(item.productId)">Rimuovi</button>
        </li>
      </ul>
      <button (click)="clear()">Svuota carrello</button>
    </div>
    <ng-template #emptyCart>
      <p>Carrello vuoto.</p>
    </ng-template>
  `,
  styles: [`
    h2 { margin-bottom: 1rem; }
    ul { list-style-type: none; padding: 0; }
    li { margin-bottom: 0.5rem; }
    button { margin-left: 1rem; }
  `]
})
export class CartComponent implements OnInit {
  userId = 1; // In un'app reale questo valore verrebbe dall'autenticazione
  cart: CartDTO | null = null;
  private cartService = inject(CartService);

  ngOnInit(): void {
    // Carica il carrello all'avvio del componente
    this.loadCart();

    // Sottoscrivi ai cambiamenti del carrello
    this.cartService.cart$.subscribe((cart: CartDTO | null) => {
      this.cart = cart;
    });
  }

  loadCart(): void {
    this.cartService.getCart(this.userId).subscribe((cart: CartDTO) => {
      this.cart = cart;
    });
  }

  remove(productId: number): void {
    this.cartService.removeItem(this.userId, productId).subscribe((cart: CartDTO) => {
      this.cart = cart;
    });
  }

  clear(): void {
    this.cartService.clearCart(this.userId).subscribe(() => {
      // Aggiorna la vista impostando il carrello vuoto
      this.cart = { userId: this.userId, items: [] };
    });
  }
}
