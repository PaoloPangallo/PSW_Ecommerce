import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormsModule} from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CheckoutService, CheckoutRequest, CheckoutResponse } from '../../services/checkout.service';
import { AuthService } from '../../services/auth.services';
import { CartService } from '../../services/cart.service';
import { CartDTO } from '../../models/cart.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Order } from '../../models/order.model';
import { OrderService } from '../../services/order.service';
import {LirePipe} from '../../services/lire.pipe';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    LirePipe,
    FormsModule,
    // Importiamo il pipe per formattare i prezzi in lire
  ],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit, OnDestroy {

  checkoutForm!: FormGroup;
  currentStep = 1;
  createdOrder: Order | null = null;

  checkoutResponse: CheckoutResponse | null = null;
  loading = false;
  error: string | null = null;
  cart: CartDTO | null = null;

  // Metodi di pagamento disponibili
  paymentMethods = [
    { value: 'Visa', label: 'Visa' },
    { value: 'PayPal', label: 'PayPal' },
    { value: 'MasterCard', label: 'MasterCard' }
  ];

  private unsubscribe$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private checkoutService: CheckoutService,
    private authService: AuthService,
    private cartService: CartService,
    private cdRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadCart();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  private initializeForm(): void {
    this.checkoutForm = this.fb.group({
      shipping: this.fb.group({
        address: ['', Validators.required],
        country: ['', Validators.required],
        city:    ['', Validators.required],
        zipCode: ['', Validators.required],
        shippingMethod: ['STANDARD', Validators.required]
      }),
      transaction: this.fb.group({
        paymentMethod: ['', Validators.required],
        amount: [this.getCartTotal(), [Validators.required, Validators.min(0)]],
        status: ['PENDING', Validators.required]
      })
    });
  }

  private loadCart(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.cartService.getCart(userId)
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe({
          next: (cart) => {
            this.cart = cart;
            // Aggiorna il campo "amount" nel form con il totale aggiornato
            this.checkoutForm.get('transaction.amount')?.setValue(this.getCartTotal());
            this.cdRef.detectChanges();
          },
          error: (err) => {
            console.error('❌ Errore nel caricamento del carrello:', err);
          }
        });
    }
  }

  getCartTotal(): number {
    if (!this.cart || !this.cart.items) {
      return 0;
    }
    return this.cart.items.reduce((acc, item) => acc + (item.price * item.quantity), 0);
  }

  // Getter per i gruppi del form
  get shippingGroup(): FormGroup {
    return this.checkoutForm.get('shipping') as FormGroup;
  }

  get transactionGroup(): FormGroup {
    return this.checkoutForm.get('transaction') as FormGroup;
  }

  nextStep(): void {
    if (this.currentStep === 2 && this.shippingGroup.invalid) {
      this.shippingGroup.markAllAsTouched();
      return;
    }
    if (this.currentStep === 3 && this.transactionGroup.invalid) {
      this.transactionGroup.markAllAsTouched();
      return;
    }
    this.currentStep++;
    this.cdRef.detectChanges();
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
      this.cdRef.detectChanges();
    }
  }

  onSubmit(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      return;
    }

    const token = this.authService.getToken();
    const userId = this.authService.getCurrentUserId();
    if (!userId || !token) {
      this.error = 'Utente non autenticato o token assente.';
      return;
    }

    this.loading = true;
    const checkoutData: CheckoutRequest = this.checkoutForm.getRawValue();

    this.checkoutService.processCheckout(userId, checkoutData, token)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (response) => {
          this.checkoutResponse = response;
          this.error = null;
          this.loading = false;

          if (response.orderId) {
            this.orderService.getOrderById(userId, response.orderId).subscribe({
              next: (order: Order | null) => {
                this.createdOrder = order;
                this.cdRef.detectChanges();
              },
              error: (err) => {
                console.error('Errore nel recupero dell\'ordine creato:', err);
              }
            });
          }

          this.cdRef.detectChanges();
        },
        error: (err) => {
          this.error = err.error?.message || 'Errore durante il checkout';
          this.loading = false;
          this.cdRef.detectChanges();
        }
      });
  }
}
