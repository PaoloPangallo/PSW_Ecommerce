import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Coupon } from '../../../models/coupon.model';
import { CouponService } from '../../../services/coupon.service';

@Component({
  selector: 'app-coupon-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './coupon-list.component.html',
  styleUrls: ['./coupon-list.component.css']
})
export class CouponListComponent implements OnInit {
  coupons: Coupon[] = [];

  constructor(private couponService: CouponService) {}

  ngOnInit() {
    this.couponService.getAllCoupons().subscribe({
      next: (res) => {
        this.coupons = res;
      },
      error: (err: any) => {
        console.error('Error fetching coupons:', err);
      }
    });
  }
}
