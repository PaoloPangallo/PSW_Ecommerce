import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {AuthService} from '../../services/auth.services';

@Component({
  selector: 'app-reclami',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reclami.component.html',
  styleUrls: ['./reclami.component.css']
})
export class ReclamiComponent {
  form: FormGroup;
  success = false;

  userEmail: string = '';

  constructor(private fb: FormBuilder, private http: HttpClient, private authService: AuthService) {
    this.userEmail = this.authService.getCurrentUserEmail() ?? 'Non disponibile';
    this.form = this.fb.group({
      category: ['', Validators.required],
      message: ['', Validators.required]
    });
  }


  submit() {
    if (this.form.valid) {
      const { category, message } = this.form.value; // ❌ niente più email

      this.http.post('http://localhost:8080/api/complaints', { category, message })
        .subscribe(() => {
          this.success = true;
          this.form.reset();
        });
    }
  }

}
