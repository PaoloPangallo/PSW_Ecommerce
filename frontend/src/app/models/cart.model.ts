// src/app/models/cart.model.ts

export interface CartItemDTO {
  id: number;         // Identificativo univoco dell'item nel carrello
  productId: number;  // Identificativo del prodotto
  productName: string;
  price: number;
  quantity: number;
}


export interface CartDTO {
  userId: number;
  items: CartItemDTO[];
}
