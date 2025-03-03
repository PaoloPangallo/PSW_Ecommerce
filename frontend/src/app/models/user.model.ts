export interface User {
  id: number;
  username: string;
  email: string;
  password?: string;
  role: 'USER' | 'ADMIN';
  phone?: string;       // Usa phone invece di phoneNumber
  address?: string;
  cap?: string;
  city?: string;
  region?: string;
  country?: string;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  phone?: string;
  address?: string;
  cap?: string;
  city?: string;
  region?: string;
  country?: string;
}
