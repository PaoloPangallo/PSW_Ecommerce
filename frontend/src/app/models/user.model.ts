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
}
