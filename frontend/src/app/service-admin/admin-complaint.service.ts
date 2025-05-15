import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Complaint } from '../models/complaint.model';

@Injectable({
  providedIn: 'root'
})
export class ComplaintService {
  private apiUrl = 'http://localhost:8080/api/complaints';

  constructor(private http: HttpClient) {}

  getAllComplaints(): Observable<Complaint[]> {
    return this.http.get<Complaint[]>(this.apiUrl);
  }

  // admin-complaint.service.ts
  updateComplaintStatus(id: number, status: string) {
    return this.http.put(
      `http://localhost:8080/api/complaints/${id}/status`,
      { status },                                   // JSON body
      { headers: { 'Content-Type': 'application/json' } } // facoltativo: default è già JSON
    );
  }



}
