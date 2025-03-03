import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {OrderService} from '../../services/order.service';

@Component({
  selector: 'app-create-order',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button (click)="createOrder()">Crea Ordine</button>
    <div *ngIf="message">{{ message }}</div>
  `
})
export class CreateOrderComponent {
  message = '';
  userId = 1; // da un servizio di auth o route param

  constructor(private orderService: OrderService) {}

  createOrder() {
    this.orderService.createOrder(this.userId).subscribe({
      next: (order) => {
        this.message = `Ordine creato con ID: ${order.id}, totale: ${order.total}`;
      },
      error: (err) => {
        this.message = 'Errore nella creazione dell’ordine: ' + (err.error || err.message);
      }
    });
  }
}
