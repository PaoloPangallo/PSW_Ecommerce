import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Order} from '../../models/order.model';
import {OrderService} from '../../services/order.service';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Storico Ordini</h2>
    <div *ngIf="error" style="color: red">{{ error }}</div>
    <div *ngIf="orders?.length === 0">Nessun ordine trovato.</div>
    <ul *ngIf="orders && orders.length > 0">
      <li *ngFor="let order of orders">
        ID: {{ order.id }} - Totale: {{ order.total }} - Data: {{ order.createdAt }}
        <!-- Esempio: link a un dettaglio ordine -->
        <!-- <button (click)="goToOrderDetails(order.id)">Dettagli</button> -->
      </li>
    </ul>
  `
})
export class OrderHistoryComponent implements OnInit {
  orders: Order[] = [];
  error = '';
  userId = 1; // In un caso reale, lo recuperi da un AuthService o da route param

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.getOrdersByUser(this.userId).subscribe({
      next: (data) => this.orders = data,
      error: (err) => {
        this.error = err.error ? err.error : 'Errore nel recupero ordini';
        console.error(err);
      }
    });
  }
}
