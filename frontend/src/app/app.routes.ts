import { TaskTableComponent } from './task-table/task-table.component';
import { Routes } from '@angular/router';
import { CalendarComponent } from './calendar/calendar.component';
import { ReportsComponent } from './reports/reports.component';

export const routes: Routes = [
  { path: 'tasks', component: TaskTableComponent },
  { path: 'calendar', component: CalendarComponent },
  { path: 'reports', component: ReportsComponent },
  { path: '', redirectTo: '/tasks', pathMatch: 'full' }, // Default route
];
