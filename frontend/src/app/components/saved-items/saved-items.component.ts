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


  moveToCart(item: any): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.isLoading = true;
    this.savedService.restoreToCart(userId, item.product.id).subscribe({
      next: (updatedCart) => {
        this.showSnack('✅ Spostato nel carrello');
        this.cartService.setCart(updatedCart);
        this.savedItems = this.savedItems.filter(i => i.product.id !== item.product.id);
      },
      error: (err) => {
        const msg = err.error?.message || 'Errore durante lo spostamento nel carrello';
        this.showSnack(msg);
      },
      complete: () => this.isLoading = false
    });
  }





  removing = new Set<number>();

  removeFromSaved(item: any): void {
    const targetProductId = +item.product.id;
    if (this.removing.has(targetProductId)) return;

    this.removing.add(targetProductId);
    console.log("🗑️ Tentativo di rimozione per productId:", targetProductId);

    this.savedService.removeSavedItem(targetProductId).subscribe({
      next: () => {
        this.showSnack('🗑️ Rimosso dai salvati');
        this.savedItems = this.savedItems.filter(i => +i.product.id !== targetProductId);
        console.log("✅ Lista aggiornata:", this.savedItems.map(i => +i.product.id));
      },
      error: (err) => {
        console.error("❌ Errore durante la rimozione:", err);
        this.showSnack('Errore nella rimozione');
      },
      complete: () => this.removing.delete(targetProductId)
    });
  }






  private showSnack(msg: string): void {
    this.snackBar.open(msg, 'OK', { duration: 2500 });
  }

  isLoading = false;

  loadSavedItems(): void {
    this.isLoading = true;
    this.savedService.getSavedItems().subscribe({
      next: (items) => {
        console.log('🎯 Items ricevuti:', items); // <== aggiungi questo
        this.savedItems = items;
      },
      error: () => this.showSnack('Errore nel caricamento degli elementi salvati'),
      complete: () => this.isLoading = false
    });
  }


}
