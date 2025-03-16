// src/app/pages/admin/admin.routes.ts
import { Routes } from '@angular/router';
import {AdminComponent} from '../admin.component';
import {AdminGuard} from '../../guards/admin.guard';


export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminComponent,
    canActivate: [AdminGuard],
    children: [
      // Se l'utente digita /admin senza nulla, lo reindirizzi a /admin/dashboard
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      // Carica la Dashboard
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },

      // Carica la pagina di gestione utenti
      {
        path: 'users',
        loadComponent: () =>
          import('./users/admin-users.component').then(m => m.AdminUsersComponent)
      },

      // Carica la pagina di gestione prodotti
      {
        path: 'products',
        loadComponent: () =>
          import('./products/admin-products.component').then(m => m.AdminProductsComponent)
      }
    ]
  }
];
