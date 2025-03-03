// src/app/components/payment-list/payment-list.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../services/payment.service';
import { PaymentResponse } from '../../models/payment.models';

@Component({
  selector: 'app-payment-list',
  templateUrl: './payment-list.component.html',
  styleUrls: ['./payment-list.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class PaymentListComponent implements OnInit {
  payments: PaymentResponse[] = [];
  error = '';

  constructor(private paymentService: PaymentService) {}

  ngOnInit() {
    this.paymentService.getAllPayments().subscribe({
      next: (data: PaymentResponse[]) => {
        this.payments = data;
      },
      error: (err) => {
        this.error = err.error || err.message;
      }
    });
  }

  deletePayment(id: number) {
    this.paymentService.deletePayment(id).subscribe({
      next: () => {
        this.payments = this.payments.filter(p => p.id !== id);
      },
      error: (err) => {
        this.error = err.error || err.message;
      }
    });
  }
}
