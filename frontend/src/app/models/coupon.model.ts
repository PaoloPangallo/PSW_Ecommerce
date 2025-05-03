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
  discountPercentage: number;
  expirationDate: string;
  isActive: boolean;
  minOrderValue: number;
  productIds: number[];
}
