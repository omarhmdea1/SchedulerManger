import {
  Component,
  signal,
  ChangeDetectorRef,
  ViewChild,
  AfterViewInit,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FullCalendarComponent,
  FullCalendarModule,
} from '@fullcalendar/angular';
import { CalendarOptions, EventClickArg, EventApi } from '@fullcalendar/core';
import interactionPlugin from '@fullcalendar/interaction';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';
import { TaskService } from '../services/task.service';
import { TaskDetailsDialogComponent } from '../task-details-dialog/task-details-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ErrorHandlingService } from '../services/errorHandling.service';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, FullCalendarModule],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css'],
})
export class CalendarComponent implements AfterViewInit {
  @ViewChild('calendar') calendarComponent!: FullCalendarComponent;

  calendarOptions = signal<CalendarOptions>({
    plugins: [interactionPlugin, dayGridPlugin, timeGridPlugin, listPlugin],
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek',
    },
    initialView: 'dayGridMonth',
    initialEvents: [], // Fetch from backend later
    weekends: true,
    editable: true,
    selectable: true,
    selectMirror: true,
    dayMaxEvents: true,
    eventClick: this.handleEventClick.bind(this),
    eventsSet: this.handleEvents.bind(this),
    datesSet: this.onDatesSet.bind(this),
  });

  currentEvents = signal<EventApi[]>([]);

  constructor(
    private changeDetector: ChangeDetectorRef,
    private taskService: TaskService,
    private dialog: MatDialog,
    private errorHandlingService: ErrorHandlingService
  ) {}

  ngAfterViewInit(): void {
    const calendarApi = this.calendarComponent.getApi();
    const viewStart = calendarApi.view.currentStart;
    const viewEnd = calendarApi.view.currentEnd;
    this.loadEvents(viewStart, viewEnd);
  }

  onDatesSet(dateInfo: any): void {
    const startDate = dateInfo.start;
    const endDate = dateInfo.end;
    this.loadEvents(startDate, endDate);
  }

  loadEvents(startDate: Date, endDate: Date): void {
    const startDateString = this.formatDateAsISO(startDate, 'start');
    const endDateString = this.formatDateAsISO(endDate, 'end');

    this.taskService
      .getTasksForPeriod(startDateString, endDateString)
      .subscribe(
        (tasks: any[]) => {
          const events = tasks.map((task) => ({
            id: task.id.toString(),
            title: task.name,
            start: task.startTime.toString(),
            end: task.endTime.toString(),
            duration: task.duration,
          }));

          this.calendarOptions.update((options) => ({
            ...options,
            events,
          }));

          this.changeDetector.detectChanges();
        },
        (error) => this.errorHandlingService.handleError(error)
      );
  }

  formatDateAsISO(date: Date, timePart: 'start' | 'end'): string {
    const datePart = date.toISOString().split('T')[0]; // YYYY-MM-DD

    if (timePart === 'start') {
      return `${datePart}T00:00:00`; // Start of the day
    } else {
      return `${datePart}T23:59:59`; // End of the day
    }
  }

  handleEventClick(clickInfo: EventClickArg) {
    this.dialog.open(TaskDetailsDialogComponent, {
      data: {
        name: clickInfo.event.title,
        startTime: clickInfo.event.start?.toLocaleString() || 'N/A',
        endTime: clickInfo.event.end?.toLocaleString() || 'N/A',
      },
    });
  }

  handleEvents(events: EventApi[]) {
    this.currentEvents.set(events);
    this.changeDetector.detectChanges();
  }

  handleWeekendsToggle() {
    this.calendarOptions.update((options) => ({
      ...options,
      weekends: !options.weekends,
    }));
  }
}
