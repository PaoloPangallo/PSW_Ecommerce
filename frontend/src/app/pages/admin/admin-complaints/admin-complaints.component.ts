import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Complaint } from '../../../models/complaint.model';
import { ComplaintService } from '../../../service-admin/admin-complaint.service';
import {AdminComplaintChatComponent} from '../admin-complaints-chat/admin-complaint-chat.component';
import {FormsModule} from '@angular/forms';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-admin-complaints',
  templateUrl: './admin-complaints.component.html',
  standalone: true,
  imports: [DatePipe, CommonModule, AdminComplaintChatComponent, FormsModule, MatTooltip],
  styleUrls: ['./admin-complaints.component.css']
})
export class AdminComplaintsComponent implements OnInit {
  complaints: Complaint[] = [];

  constructor(private complaintService: ComplaintService) {}

  ngOnInit(): void {
    this.complaintService.getAllComplaints().subscribe(data => {
      this.complaints = data;
    });
  }

  selectedComplaintId: number | null = null;

  selectComplaint(id: number) {
    this.selectedComplaintId = id;
  }

  updateStatus(id: number, status: string) {
    const upper = status.toUpperCase();
    this.complaintService.updateComplaintStatus(id, upper).subscribe({
      next: () => console.log(`✅ Stato aggiornato per reclamo #${id}`),
      error: err => console.error('❌ Errore aggiornamento stato', err)
    });
  }





  isTerminal(status: string): boolean {
    return status === 'REJECTED' || status === 'CLOSED';
  }

  badgeClass(status: string): string {
    return {
      PENDING:  'status-pending',
      ACCEPTED: 'status-accepted',
      REJECTED: 'status-rejected',
      CLOSED:   'status-closed'
    }[status] ?? 'status-other';
  }



}
