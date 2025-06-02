import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminCouponService } from '../../../service-admin/admin-coupon.service';
import { Coupon, CouponCreationDTO } from '../../../models/coupon.model';
import { Page } from '../../../models/page.model';

@Component({
  selector: 'app-admin-coupons',
  templateUrl: './admin-coupons.component.html',
  styleUrls: ['./admin-coupons.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class AdminCouponsComponent implements OnInit {
  coupons: Coupon[] = [];
  createMode = false;

  currentPage = 0;
  pageSize = 5;
  totalPages = 0;

  newCoupon: Partial<CouponCreationDTO> = {
    code: '',
    discountPercentage: 0,
    expirationDate: '',
    isActive: true,
    minOrderValue: 0,
    productIds: []
  };
  productIdsString: string = '';

  constructor(
    private adminCouponService: AdminCouponService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCoupons();
    setTimeout(() => this.loadCoupons(), 200); // ⚠️ simula seconda chiamata (da cache)
  }

  loadCoupons(): void {
    this.adminCouponService.getCoupons(this.currentPage, this.pageSize).subscribe(
      (data: Page<Coupon>) => {
        console.log("📦 Risposta ricevuta:", data); // 👈
        this.coupons = data.content;
        this.totalPages = data.totalPages;
      },
      error => console.error('❌ Errore nel recupero dei coupon', error)
    );
  }


  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadCoupons();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadCoupons();
    }
  }

  onChangePageSize(size: number): void {
    this.pageSize = size;
    this.currentPage = 0;
    this.loadCoupons();
  }

  goBack(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  onCreate(): void {
    this.createMode = true;
    this.newCoupon = {
      code: '',
      discountPercentage: 0,
      expirationDate: '',
      isActive: true,
      minOrderValue: 0,
      productIds: []
    };
    this.productIdsString = '';
  }

  cancelCreate(): void {
    this.createMode = false;
  }

  onSubmitCreate(): void {
    if (this.productIdsString.trim() !== '') {
      this.newCoupon.productIds = this.productIdsString.split(',')
        .map(id => parseInt(id.trim()))
        .filter(id => !isNaN(id));
    } else {
      this.newCoupon.productIds = [];
    }

    this.adminCouponService.createCoupon(this.newCoupon as CouponCreationDTO).subscribe(
      () => {
        this.createMode = false;
        this.loadCoupons(); // ricarica lista
      },
      error => console.error('Errore nella creazione del coupon', error)
    );
  }
}
