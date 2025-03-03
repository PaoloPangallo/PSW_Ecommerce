import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // IMPORT

import { PaymentService } from '../../services/payment.service';
import { PaymentRequestDTO, PaymentResponse } from '../../models/payment.models';

@Component({
  selector: 'app-create-payment',
  templateUrl: './create-payment.component.html',
  styleUrls: ['./create-payment.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule // AGGIUNGI QUI
  ]
})
export class CreatePaymentComponent {
  paymentRequest: PaymentRequestDTO = {
    userId: 0,
    orderId: 0,
    paymentMethod: '',
    amount: 0
  };

  message = '';

  constructor(private paymentService: PaymentService) {}

  createPayment() {
    this.paymentService.createPayment(this.paymentRequest).subscribe({
      next: (response: PaymentResponse) => {
        this.message = `Pagamento creato con ID: ${response.id}, stato: ${response.status}`;
      },
      error: (err) => {
        this.message = `Errore nella creazione del pagamento: ${err.error || err.message}`;
      }
    });
  }
}
