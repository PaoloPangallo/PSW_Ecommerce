export interface Review {
  id: number;
  rating: number;
  comment: string;
  createdDate: string;
  user: {
    id: number;
    name: string;
  };
}


export interface ReviewDTO {
  id: number;
  rating: number;
  comment: string;
  productId: number;
  userId: number;
  createdDate: string;
  userName: string;
  upvotesCount: number; // nuovo campo per il conteggio degli upvote
  hasUpvoted: boolean;
  imageUrls: string[];
// flag per indicare se l'utente corrente ha votato
}
