// src/app/models/order.model.ts
export interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

export interface Order {
  id: number;
  total: number;
  createdAt: string;    // o Date, se preferisci convertirlo
  items: OrderItem[];
}

// src/app/models/page.model.ts
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;        // pagina corrente (0-based)
  size: number;          // numero di elementi per pagina
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

