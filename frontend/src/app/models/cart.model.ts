// src/app/models/cart.model.ts

export interface CartItemDTO {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  price: number;      // Prezzo scontato (se applicato)
  oldPrice?: number;  // Prezzo originale (se presente e maggiore del prezzo scontato)
  couponCode?: string;
  discountPercentage?: number;
  discountedPrice?: number;
  originalPrice?: number;
}


export interface CartDTO {
  userId: number;
  items: CartItemDTO[];
}
