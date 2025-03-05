import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Order } from '../../models/order.model';
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
  orders: Order[] = [];
  error = '';
  isLoading = true;
  displayedColumns: string[] = ['id', 'total', 'date', 'actions'];

  private orderService = inject(OrderService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.error = 'Nessun utente autenticato!';
      this.isLoading = false;
      return;
    }

    this.orderService.getOrdersByUser(userId).subscribe({
      next: (data) => {
        // Ordina gli ordini in base alla data in ordine decrescente
        this.orders = data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Errore nel recupero ordini';
        console.error(err);
        this.isLoading = false;
      }
    });

  }}
