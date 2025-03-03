// order.model.ts
export interface Order {
  id: number;
  total: number;            // BigDecimal lato Java, ma come number in TS
  createdAt: string;        // string ISO date (es. "2023-01-01T12:00:00")
  userId?: number;          // se lo vuoi mostrare nel frontend
  // ... altri campi se necessari (es. status, items, ecc.)
}
