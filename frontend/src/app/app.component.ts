import { TaskService } from './services/task.service';
import { Component, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { FullCalendarModule } from '@fullcalendar/angular';
import {
  CalendarOptions,
  DateSelectArg,
  EventClickArg,
  EventApi,
} from '@fullcalendar/core';
import interactionPlugin from '@fullcalendar/interaction';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';
import { createEventId } from './event-utils'; // Adjust as needed based on your utils structure
import { SchedulerComponent } from './scheduler/scheduler.component';
import { SidebarComponent } from './sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    FullCalendarModule,
    SchedulerComponent,
    SidebarComponent,
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  calendarVisible = signal(true);
  calendarOptions = signal<CalendarOptions>({
    plugins: [interactionPlugin, dayGridPlugin, timeGridPlugin, listPlugin],
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek',
    },
    initialView: 'dayGridMonth',
    initialEvents: [], // Start with an empty list and fetch from the backend
    weekends: true,
    editable: true,
    selectable: true,
    selectMirror: true,
    dayMaxEvents: true,
    select: this.handleDateSelect.bind(this),
    eventClick: this.handleEventClick.bind(this),
    eventsSet: this.handleEvents.bind(this),
  });
  currentEvents = signal<EventApi[]>([]);

  constructor(
    private changeDetector: ChangeDetectorRef,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    // Fetch tasks and update calendar
    const startDate = '2024-09-01T00:00:00';
    const endDate = '2024-09-30T23:59:59';

    this.taskService.getTasksForPeriod(startDate, endDate).subscribe(
      (tasks: any[]) => {
        const events = tasks.map((task) => ({
          id: task.id.toString(),
          title: task.name,
          start: task.startTime.toString(),
          end: task.endTime.toString(),
        }));

        this.calendarOptions.update((options) => ({
          ...options,
          events,
        }));
        this.changeDetector.detectChanges(); // Detect changes to update the view
      },
      (error) => {
        console.error('Error fetching tasks', error);
      }
    );
  }

  // Example method to get the start date for the current calendar view
  getCalendarStartDate(): string {
    // const calendarApi = this.calendarOptions().initialView // Adjust according to your calendar configuration
    // const currentStart = calendarApi.view.currentStart // Assuming FullCalendar API supports this
    // return currentStart.toISOString() // Convert to string format suitable for the backend
    return '2024-09-14T10:00:00';
  }

  // Example method to get the end date for the current calendar view
  getCalendarEndDate(): string {
    // const calendarApi = this.calendarOptions().initialView // Adjust according to your calendar configuration
    // const currentEnd = calendarApi.view.currentEnd // Assuming FullCalendar API supports this
    // return currentEnd.toISOString() // Convert to string format suitable for the backend
    return '2024-09-20T11:00:00';
  }

  handleCalendarToggle() {
    this.calendarVisible.update((bool) => !bool);
  }

  handleWeekendsToggle() {
    this.calendarOptions.update((options) => ({
      ...options,
      weekends: !options.weekends,
    }));
  }

  handleDateSelect(selectInfo: DateSelectArg) {
    const title = prompt('Please enter a new title for your event');
    const calendarApi = selectInfo.view.calendar;

    calendarApi.unselect(); // clear date selection

    if (title) {
      calendarApi.addEvent({
        id: createEventId(),
        title,
        start: selectInfo.startStr,
        end: selectInfo.endStr,
        allDay: selectInfo.allDay,
      });
    }
  }

  handleEventClick(clickInfo: EventClickArg) {
    if (
      confirm(
        `Are you sure you want to delete the event '${clickInfo.event.title}'`
      )
    ) {
      clickInfo.event.remove();
    }
  }

  handleEvents(events: EventApi[]) {
    this.currentEvents.set(events);
    this.changeDetector.detectChanges(); // Detect changes after event updates
  }
}
