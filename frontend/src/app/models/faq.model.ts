export interface Faq {
  id: number;
  question: string;
  answer: string;
  open?: boolean; // ← campo opzionale per l'accordion
}
