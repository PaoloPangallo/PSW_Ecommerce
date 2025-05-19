import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.services';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import {SavedService} from '../../services/saved-for-later.service';

@Component({
  selector: 'app-saved-items',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './saved-items.component.html',
  styleUrls: ['./saved-items.component.css']
})
export class SavedItemsComponent implements OnInit {
  savedItems: any[] = [];
  private savedService = inject(SavedService);
  private cartService = inject(CartService);
  private authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    this.loadSavedItems();
  }

  loadSavedItems(): void {
    this.savedService.getSavedItems().subscribe({
      next: (items) => this.savedItems = items,
      error: (err) => this.showSnack('Errore nel caricamento degli elementi salvati')
    });
  }

  moveToCart(item: any): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.cartService.addToCart(userId, item.product.id, item.quantity).subscribe({
      next: () => {
        this.savedService.removeSavedItem(item.product.id).subscribe({
          next: () => {
            this.showSnack('✅ Spostato nel carrello');
            this.loadSavedItems();
          },
          error: () => this.showSnack('Errore nella rimozione dai salvati')
        });
      },
      error: () => this.showSnack('Errore nell’aggiunta al carrello')
    });
  }

  removeFromSaved(item: any): void {
    this.savedService.removeSavedItem(item.product.id).subscribe({
      next: () => {
        this.showSnack('🗑️ Rimosso dai salvati');
        this.loadSavedItems();
      },
      error: () => this.showSnack('Errore nella rimozione')
    });
  }

  private showSnack(msg: string): void {
    this.snackBar.open(msg, 'OK', { duration: 2500 });
  }
}
