import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, Type } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private baseUrl = 'http://localhost:8080/api/tasks'; // Base URL of your backend

  constructor(private http: HttpClient) {}

  getTasksForPeriod(startDate: string, endDate: string): Observable<any[]> {
    let params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<any[]>(`${this.baseUrl}/list/byPeriod`, { params });
  }

  createTask(taskData: any): Observable<any> {
    const taskToSend = {
      ...taskData,
      startTime: new Date(taskData.startTime),
      endTime: new Date(taskData.endTime),
    };
    return this.http.post<any>(`${this.baseUrl}/schedule`, taskToSend);
  }

  combineDateTime(dateNow: string, newTime: string): string {
    const datePart = new Date(dateNow).toISOString().split('T')[0];
    const combinedDateTimeString = `${datePart}T${newTime}`;
    return combinedDateTimeString;
  }

  updateTask(taskData: any): Observable<any> {
    const taskToSend = {
      ...taskData,
      startTime: new Date(taskData.startTime),
      endTime: new Date(taskData.endTime),
    };
    return this.http.put<any>(`${this.baseUrl}/${taskData.id}`, taskToSend);
  }

  getTasks(): Observable<any[]> {
    let startDate = new Date().toISOString();
    let params = new HttpParams().set('startDate', startDate);
    return this.http.get<any[]>(`${this.baseUrl}/list/byStartDate`, { params });
  }

  getTaskReports() {
    return this.http.get<any[]>(`${this.baseUrl}/reports`);
  }

  deleteTask(taskId: any) {
    return this.http.delete<void>(`${this.baseUrl}/${taskId}`);
  }
}
