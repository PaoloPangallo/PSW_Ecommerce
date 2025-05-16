import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Order, Page } from '../../models/order.model';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.services';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { LirePipe } from '../../services/lire.pipe';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    RouterLink,
    LirePipe
  ],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent implements OnInit {
  orders: Order[] = [];
  error = '';
  isLoading = true;
  displayedColumns: string[] = ['id', 'total', 'date', 'status', 'download', 'actions'];

  currentPage = 0;
  pageSize = 5;
  totalPages = 0;

  userId: number | null = null;



  private orderService = inject(OrderService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUserId();
    this.loadOrders(this.currentPage);
  }

  constructor(
    private http: HttpClient
  ) {}



  loadOrders(page: number): void {
    if (!this.userId) {
      this.error = 'Nessun utente autenticato!';
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.orderService.getOrdersByUserPaginated(this.userId, page, this.pageSize).subscribe({
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


  downloadInvoice(orderId: number): void {
    const userId = this.authService.getCurrentUserId();
    const url = `http://localhost:8080/users/${userId}/orders/${orderId}/invoice`;

    this.http.get(url, { responseType: 'blob' }).subscribe(blob => {
      const file = new Blob([blob], { type: 'application/pdf' });
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(file);
      link.download = `fattura_ordine_${orderId}.pdf`;
      link.click();
      window.URL.revokeObjectURL(link.href);
    });
  }

}
