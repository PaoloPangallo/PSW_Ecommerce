// src/app/models/wishlist.model.ts
import { Product } from './product.model';

export interface Wishlist {
  id: number;
  userId: number;
  products: Product[];
}
