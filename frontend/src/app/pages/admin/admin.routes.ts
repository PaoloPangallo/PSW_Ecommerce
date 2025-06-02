// src/app/pages/admin/admin.routes.ts
import { Routes } from '@angular/router';
import { AdminComponent } from '../admin.component';
import { AdminGuard } from '../../guards/admin.guard';
import {AdminNewsletterComponent} from './newsletter/admin-newsletter/admin-newsletter.component';
import {AdminDiscountComponent} from './product-form/admin-discount.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminComponent,
    canActivate: [AdminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      // Dashboard
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'reported-reviews',
        loadComponent: () => import('./reported-reviews/admin-reported-reviews.component').then(m => m.AdminReportedReviewsComponent)
      },

      { path: 'discounts', component: AdminDiscountComponent },





      // Gestione utenti
      {
        path: 'users',
        loadComponent: () =>
          import('./users/admin-users.component').then(m => m.AdminUsersComponent)
      },

      // Gestione prodotti
      {
        path: 'products',
        loadComponent: () =>
          import('./products/admin-products.component').then(m => m.AdminProductsComponent)
      },

      // Gestione coupon
      {
        path: 'coupons',
        loadComponent: () =>
          import('./coupon/admin-coupons.component').then(m => m.AdminCouponsComponent)
      },

      {
        path: 'complaints',
        loadComponent: () =>
          import('./admin-complaints/admin-complaints.component')
            .then(m => m.AdminComplaintsComponent)
      },



      // Generazione immagine per prodotto (già esistente)
      {
        path: 'generate-image/:productId',
        loadComponent: () =>
          import('./product-image-generator/product-image-generator.component')
            .then(m => m.ProductImageGeneratorComponent)
      },

      { path: 'newsletter', component: AdminNewsletterComponent },


      {
        path: 'home-photo',
        loadComponent: () =>
          import('./photo/admin-home-photo.component').then(m => m.AdminHomePhotoComponent)
      }
    ]
  }
];
