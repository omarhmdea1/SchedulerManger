import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'scheduler',
  standalone: true,
  imports: [RouterModule, SidebarComponent],
  templateUrl: './scheduler.component.html',
  styleUrl: './scheduler.component.css',
})
export class SchedulerComponent {}
