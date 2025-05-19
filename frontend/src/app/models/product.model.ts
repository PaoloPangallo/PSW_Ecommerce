export interface Product {
  id?: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  category?: { name: string };
  imageUrl?: string;
  featured?: boolean;
  discountedPrice: number;
  discountPercentage: number;
}
