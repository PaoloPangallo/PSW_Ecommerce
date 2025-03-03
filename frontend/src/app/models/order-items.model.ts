// order-item.model.ts
export interface OrderItem {
  id: number;
  productId: number;
  orderId: number;
  quantity: number;
  price: number; // BigDecimal -> number in TS
  // ... eventuali campi aggiuntivi
}
