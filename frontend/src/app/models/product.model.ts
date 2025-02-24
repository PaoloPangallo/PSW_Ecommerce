export interface Product {
  id?: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  category?: { name: string };  // ✅ Categoria opzionale
  imageUrl?: string;
}
