export interface Coupon {
  id?: number;
  code: string;
  discountPercentage: number;
  expirationDate: string;   // Puoi usare Date se gestisci la conversione
  isActive: boolean;
  minOrderValue: number;
  productNames?: string[]; // Aggiungi questo
}




export interface CouponCreationDTO {
  code: string;
  discountPercentage: number;  // Usa number se preferisci
  expirationDate: string;      // ISO string (es. "2025-12-31T23:59:59")
  isActive: boolean;
  minOrderValue: number;       // Usa number
  productIds: number[];        // ID dei prodotti a cui associare il coupon
}
