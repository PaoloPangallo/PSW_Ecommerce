// src/app/models/payment.models.ts
export interface PaymentRequestDTO {
  userId: number;
  orderId: number;
  paymentMethod: string;
  amount: number;
}

export interface PaymentResponse {
  id: number;
  userId: number;
  orderId: number;
  paymentMethod: string;
  amount: number;
  status: string;
  timestamp: string;
}
