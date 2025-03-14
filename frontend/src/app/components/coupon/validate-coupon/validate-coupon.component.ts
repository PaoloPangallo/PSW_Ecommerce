import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CouponService } from '../../../services/coupon.service';

@Component({
  selector: 'app-validate-coupon',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './validate-coupon.component.html',
  styleUrls: ['./validate-coupon.component.css']
})
export class ValidateCouponComponent {
  validateForm: FormGroup;
  isValid: boolean | null = null;

  constructor(private fb: FormBuilder, private couponService: CouponService) {
    this.validateForm = this.fb.group({
      code: ['', Validators.required],
      orderValue: [0, Validators.required]
    });
  }

  onSubmit() {
    if (this.validateForm.valid) {
      const { code, orderValue } = this.validateForm.value;
      this.couponService.validateCoupon(code, orderValue).subscribe({
        next: (res) => {
          this.isValid = res;
        },
        error: (err: any) => {
          console.error('Error validating coupon:', err);
          this.isValid = false;
        }
      });
    }
  }
}
