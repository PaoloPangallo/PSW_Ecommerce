import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartDTO, CartItemDTO } from '../../models/cart.model';
import { AuthService } from '../../services/auth.services';
import { OrderService } from '../../services/order.service';
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
    RouterModule,
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
  // Se vuoi il goBack() col pulsante 'Indietro':
  // private location = inject(Location);

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.showSnack('Nessun utente loggato.', 3000);
      return;
    }
    this.loadCart(userId);
  }

  /**
   * Carica il carrello dell'utente
   */
  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe({
      next: (cart) => {
        this.cart = cart;
      },
      error: (err) => {
        console.error('Errore nel caricamento del carrello:', err);
        this.showSnack(err.message, 5000);
      }
    });
  }

  /**
   * Aumenta la quantità di un prodotto di 1 unità
   */
  increaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;

    // Se vuoi gestire lo stock, puoi trovare l'item e controllare item.stock
    // const item = this.cart?.items.find(i => i.productId === productId);
    // if (item && item.stock && currentQuantity >= item.stock) {
    //   this.showSnack('Stock esaurito o insufficiente.', 3000);
    //   return;
    // }

    const newQuantity = currentQuantity + 1;
    this.updateItemQuantity(userId, productId, newQuantity);
  }

  /**
   * Diminuisce la quantità di un prodotto di 1 unità
   */
  decreaseQuantity(productId: number, currentQuantity: number): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;

    const newQuantity = currentQuantity - 1;
    // Se la nuova quantità è <= 0, rimuoviamo direttamente il prodotto
    if (newQuantity <= 0) {
      this.remove(productId);
    } else {
      this.updateItemQuantity(userId, productId, newQuantity);
    }
  }

  /**
   * Rimuove un prodotto dal carrello
   */
  remove(productId: number): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;

    this.cartService.removeItem(userId, productId).subscribe({
      next: (updatedCart) => {
        // Se il backend restituisce il carrello aggiornato
        // this.cart = updatedCart;

        // Altrimenti, se non restituisce nulla, ricarica manualmente
        this.loadCart(userId);
      },
      error: (err) => {
        console.error('Errore nella rimozione:', err);
        this.showSnack(err.message, 5000);
      }
    });
  }

  /**
   * Svuota l'intero carrello
   */
  clear(): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;

    this.cartService.clearCart(userId).subscribe({
      next: (updatedCart) => {
        // this.cart = updatedCart; // Se il backend lo restituisce
        this.loadCart(userId);
      },
      error: (err) => {
        console.error('Errore nello svuotamento del carrello:', err);
        this.showSnack(err.message, 5000);
      }
    });
  }

  /**
   * Crea un ordine a partire dal carrello
   */
  createOrder(): void {
    const userId = this.checkUserLoggedIn();
    if (!userId) return;

    this.orderService.createOrder(userId).subscribe({
      next: (order) => {
        this.showSnack(`Ordine creato con successo. ID: ${order.id}`, 3000);
        this.clear();
      },
      error: (err) => {
        console.error('Errore nella creazione dell\'ordine:', err);
        this.showSnack(err.error || err.message, 5000);
      }
    });
  }

  /**
   * Vai al checkout
   */
  goToCheckout(): void {
    this.router.navigate(['/checkout']);
  }

  /**
   * Ritorna alla pagina precedente (se vuoi)
   */
  // goBack(): void {
  //   this.location.back();
  // }

  /**
   * Calcola il totale del carrello
   */
  getTotalPrice(): number {
    return this.cart?.items.reduce((total, item) => total + item.price * item.quantity, 0) || 0;
  }

  /**
   * Metodo helper per aggiornare la quantità di un item nel carrello
   */
  private updateItemQuantity(userId: number, productId: number, newQuantity: number): void {
    this.cartService.updateItemQuantity(userId, productId, newQuantity).subscribe({
      next: (updatedCart) => {
        // Se il backend restituisce il carrello aggiornato, assegna qui
        // this.cart = updatedCart;

        // Altrimenti ricarica manualmente
        this.loadCart(userId);
      },
      error: (err) => {
        console.error("Errore nell'aggiornamento della quantità:", err);
        const errorMessage = err.error?.message || 'Si è verificato un errore imprevisto';
        this.showSnack(errorMessage, 5000);
      }
    });
  }

  /**
   * Verifica che l'utente sia loggato, ritorna l'ID o null se non loggato
   */
  private checkUserLoggedIn(): number | null {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.showSnack('Nessun utente loggato.', 3000);
      return null;
    }
    return userId;
  }

  /**
   * Mostra uno snackbar con messaggio e durata
   */
  private showSnack(message: string, duration: number = 3000): void {
    this.snackBar.open(message, 'Chiudi', { duration });
  }
}
