// src/app/models/cart.model.ts

export interface CartItemDTO {
  productId: number;
  productName: string;
  price: number;
  quantity: number;
}

export interface CartDTO {
  userId: number;
  items: CartItemDTO[];
}
