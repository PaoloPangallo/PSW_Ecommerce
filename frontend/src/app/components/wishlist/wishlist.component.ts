import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, NgIf, NgFor } from '@angular/common';
import { Wishlist } from '../../models/wishlist.model';
import { WishlistService } from '../../services/wishlist.service';
import { AuthService } from '../../services/auth.services';
import {LirePipe} from '../../services/lire.pipe';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css'],
  // Aggiungi la pipe all'array imports
  imports: [CommonModule, NgIf, NgFor, LirePipe],})


export class WishlistComponent implements OnInit {
  wishlist: Wishlist | null = null;
  isLoading = false;
  errorMessage = '';

  constructor(
    private wishlistService: WishlistService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadWishlist();
  }

  loadWishlist(): void {
    this.isLoading = true;
    const userId = this.authService.getCurrentUserId();

    if (!userId) {
      this.errorMessage = "Nessun utente loggato.";
      this.isLoading = false;
      return;
    }

    this.wishlistService.getUserWishlist(userId).subscribe({
      next: (wishlist) => {
        this.wishlist = wishlist;
        this.isLoading = false;
      },
      error: (err) => {
        if (err.status === 404) {
          this.errorMessage = "Nessuna wishlist trovata.";
        } else {
          this.errorMessage = "Errore nel caricamento della wishlist.";
        }
        this.isLoading = false;
      }
    });
  }

  removeProduct(productId: number): void {
    if (!this.wishlist) return;

    this.wishlistService.removeProductFromWishlist(this.wishlist.id, productId).subscribe({
      next: (updatedWishlist) => {
        this.wishlist = updatedWishlist;
      },
      error: (err) => {
        console.error("Errore rimuovendo il prodotto:", err);
      }
    });
  }

  deleteWishlist(): void {
    if (!this.wishlist) return;

    this.wishlistService.deleteWishlist(this.wishlist.id).subscribe({
      next: () => {
        this.wishlist = null;
        this.errorMessage = "Wishlist eliminata.";
      },
      error: (err) => {
        console.error("Errore eliminando la wishlist:", err);
      }
    });
  }
}
