import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { ProductDetailsComponent } from './components/product-details/product-details.component';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { CartComponent } from './components/cart/cart.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { CreatePaymentComponent } from './components/create-payment/create-payment.component';
import { PaymentListComponent } from './components/payment-list/payment-list.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { WishlistComponent } from './components/wishlist/wishlist.component';
import { OrderDetailsComponent } from './components/order/order-details/order-details.component';



export const appRoutes: Routes = [
  { path: '', component: HomeComponent },  // ✅ HomePage ora mostra HomeComponent
  { path: 'login', component: LoginComponent },
  { path: 'products', component: ProductListComponent },  // ✅ Sposta la lista prodotti in `/products`
  { path: 'order-history', component: OrderHistoryComponent },
  { path: 'product/:id', component: ProductDetailsComponent },
  { path: 'category/:categoryName', component: ProductListComponent },
  { path: 'create-payment', component: CreatePaymentComponent },
  { path: 'payments', component: PaymentListComponent },
  { path: 'cart', component: CartComponent },
  { path: 'user-profile', component: UserProfileComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'wishlist', component: WishlistComponent },
  { path: 'orders/:id', component: OrderDetailsComponent },
  { path: 'orders', component: OrderHistoryComponent },



  { path: '**', redirectTo: '' }, // Se la rotta non esiste, torna alla home
];
