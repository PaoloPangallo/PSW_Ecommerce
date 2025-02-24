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
  // Eventuali altre proprietà, ad esempio 'total'
}

export class CartItem {
  constructor(
    public productId: number,
    public productName: string,
    public price: number,
    public quantity: number
  ) {}

  get total(): number {
    return this.price * this.quantity;
  }
}

export class Cart {
  items: CartItem[] = [];

  constructor(public userId: number) {}

  addItem(newItem: CartItem): void {
    const existingItem = this.items.find(item => item.productId === newItem.productId);
    if (existingItem) {
      existingItem.quantity += newItem.quantity;
    } else {
      this.items.push(newItem);
    }
  }

  removeItem(productId: number): void {
    this.items = this.items.filter(item => item.productId !== productId);
  }

  clearCart(): void {
    this.items = [];
  }

  get total(): number {
    return this.items.reduce((acc, item) => acc + item.total, 0);
  }
}
