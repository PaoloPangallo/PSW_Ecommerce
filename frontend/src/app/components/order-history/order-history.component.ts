import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Order, Page} from '../../models/order.model';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.services';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, RouterLink],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent implements OnInit {
  orders: Order[] = [];  // Array estratto da data.content
  error = '';
  isLoading = true;
  displayedColumns: string[] = ['id', 'total', 'date', 'actions'];

  // Proprietà per la paginazione
  currentPage = 0;
  pageSize = 5;  // Numero di ordini per pagina
  totalPages = 0;

  private orderService = inject(OrderService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.loadOrders(this.currentPage);
  }

  loadOrders(page: number): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.error = 'Nessun utente autenticato!';
      this.isLoading = false;
      return;
    }
    this.isLoading = true;

    // Chiamata al servizio che restituisce un oggetto Page<Order>
    this.orderService.getOrdersByUserPaginated(userId, page, this.pageSize).subscribe({
      next: (data: Page<Order>) => {
        this.orders = data.content;
        this.currentPage = data.number;
        this.totalPages = data.totalPages;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Errore nel recupero ordini';
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  goToPreviousPage(): void {
    if (this.currentPage > 0) {
      this.loadOrders(this.currentPage - 1);
    }
  }

  goToNextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.loadOrders(this.currentPage + 1);
    }
  }
}
