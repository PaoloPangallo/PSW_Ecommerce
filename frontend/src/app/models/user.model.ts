export interface User {
  id: number;
  username: string;
  email: string;
  password?: string;
  role: 'USER' | 'ADMIN';
  phone?: string;
  address?: string;
  cap?: string;
  city?: string;
  region?: string;
  country?: string;
  profileImageUrl?: string; // ✅ nuovo campo per la foto profilo

}

// user-profile-summary.model.ts
export interface UserProfileSummary {
  username: string;
  email: string;
  city: string;
  country: string;
  totalOrders: number;
  totalSpent: number;
  wishlistCount: number;
  reviewsCount: number;


}

