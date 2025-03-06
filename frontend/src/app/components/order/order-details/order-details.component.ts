import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Order } from '../../../models/order.model';
import { OrderService } from '../../../services/order.service';
import { AuthService } from '../../../services/auth.services';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatListModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {
  order: Order | null = null;
  error = '';
  isLoading = true;

  private route = inject(ActivatedRoute);
  private orderService = inject(OrderService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    const orderIdParam = this.route.snapshot.paramMap.get('id');
    const userId = this.authService.getCurrentUserId();

    if (!userId) {
      this.error = 'Nessun utente autenticato!';
      this.isLoading = false;
      return;
    }
    if (!orderIdParam) {
      this.error = 'ID ordine non valido!';
      this.isLoading = false;
      return;
    }
    const orderId = parseInt(orderIdParam, 10);

    this.orderService.getOrderById(userId, orderId).subscribe({
      next: (data) => {
        console.log('Risposta dal backend:', data); // <--- LOGGA QUI

        this.order = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Errore nel recupero dei dettagli dell\'ordine';
        console.error(err);
        this.isLoading = false;
      }
    });
  }
}
