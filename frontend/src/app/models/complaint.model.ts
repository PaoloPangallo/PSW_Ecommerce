export interface Complaint {
  id: number;
  email: string;
  category: 'PRODOTTO_DIFETTOSO' | 'CONSEGNA_IN_RITARDO' | 'ORDINE_ERRATO' | 'SERVIZIO_CLIENTI' | 'ALTRO';
  message?: string; // se lo hai nel backend
  status: 'PENDING' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
  createdAt: string;
}
