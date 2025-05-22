import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CheckoutService, CheckoutRequest, CheckoutResponse } from '../../services/checkout.service';
import { AuthService } from '../../services/auth.services';
import { CartService } from '../../services/cart.service';
import { CartDTO } from '../../models/cart.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Order } from '../../models/order.model';
import { OrderService } from '../../services/order.service';
import { LirePipe } from '../../services/lire.pipe';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    LirePipe,
    FormsModule
  ],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit, OnDestroy {

  checkoutForm!: FormGroup;
  currentStep = 1;
  createdOrder: Order | null = null;
  estimatedDelivery: Date | null = null;


  checkoutResponse: CheckoutResponse | null = null;
  loading = false;
  error: string | null = null;
  cart: CartDTO | null = null;

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


  get shippingCost(): number {
    if (!this.cart || !this.checkoutForm) return 0;
    const method = this.checkoutForm.get('shipping.shippingMethod')?.value;
    const total = this.getCartTotal();
    if (total >= 100) return 0;

    const costMap = {
      STANDARD: 4.99,
      EXPRESS: 9.99,
      PREMIUM: 14.99
    };

    return costMap[method as keyof typeof costMap] ?? 0;
  }



  get finalTotal(): number {
    return this.getCartTotal() + this.shippingCost;
  }


  ngOnInit(): void {
    this.initializeForm();
    this.loadCart();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  private initializeForm(): void {
    // Form che rispecchia la struttura di CheckoutRequest:
    // shipping: { ... }
    // transaction: { paymentMethod, amount, status }
    this.checkoutForm = this.fb.group({
      shipping: this.fb.group({
        address: ['', Validators.required],
        country: ['', Validators.required],
        city:    ['', Validators.required],
        zipCode: ['', Validators.required],
        shippingMethod: ['STANDARD', Validators.required]
      }),
      transaction: this.fb.group({
        paymentMethod: ['', Validators.required],  // <-- Campo di primo livello
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
            // Aggiorna il campo "amount" con il totale
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
    if (this.loading) return; // 🔐 Protezione sincrona contro doppi clic

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

    this.loading = true; // ✅ Appena dopo il blocco, prima di qualsiasi async

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


  onZipCodeChange(): void {
    const cap = this.shippingGroup.get('zipCode')?.value;
    if (cap && cap.length >= 5) {
      this.checkoutService.getDeliveryEstimate(cap).subscribe({
        next: (date: Date) => {
          this.estimatedDelivery = new Date(date);
          this.cdRef.detectChanges();
        },
        error: (err) => {
          console.error('Errore nella stima della consegna:', err);
          this.estimatedDelivery = null;
        }
      });
    }
  }

}
