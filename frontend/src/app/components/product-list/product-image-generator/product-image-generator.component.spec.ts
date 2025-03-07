import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductImageGeneratorComponent } from './product-image-generator.component';

describe('ProductImageGeneratorComponent', () => {
  let component: ProductImageGeneratorComponent;
  let fixture: ComponentFixture<ProductImageGeneratorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductImageGeneratorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductImageGeneratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
