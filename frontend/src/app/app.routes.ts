import { Routes } from '@angular/router';
import { ProductListComponent } from './components/product-list/product-list.component';
import { ProductDetailsComponent } from './components/product-details/product-details.component';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { CartComponent } from './components/cart/cart.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';


export const appRoutes: Routes = [
  { path: '', component: ProductListComponent },
  { path: 'login', component: LoginComponent }, // <--- Eccola!
  { path: 'product/:id', component: ProductDetailsComponent },
  { path: 'category/:categoryName', component: ProductListComponent },
  { path: 'cart', component: CartComponent },
  { path: 'order-history', component: OrderHistoryComponent },
  { path: 'user-profile', component: UserProfileComponent },
  { path: 'register', component: RegisterComponent }, // <--- rotta per la registrazione

  { path: '**', redirectTo: '' },
];
