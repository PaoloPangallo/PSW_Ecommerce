export interface Coupon {
  id?: number;
  code: string;
  discountPercentage: number;
  expirationDate: string;   // Puoi usare Date se gestisci la conversione
  isActive: boolean;
  minOrderValue: number;
  // Se il coupon è applicabile a determinati prodotti, puoi aggiungere ad esempio:
  productIds?: number[]; // Lista degli ID dei prodotti applicabili
}
