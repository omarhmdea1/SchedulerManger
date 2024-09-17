package com.tutofox.seraj_hw.controller;

import com.tutofox.seraj_hw.dto.TaskReportDTO;
import com.tutofox.seraj_hw.dto.TaskRequest;
import com.tutofox.seraj_hw.entities.Task;
import com.tutofox.seraj_hw.entities.TaskOccurrence;
import com.tutofox.seraj_hw.exception.OverlappingTaskException;
import com.tutofox.seraj_hw.service.TaskOccurrenceService;
import com.tutofox.seraj_hw.service.TaskReportService;
import com.tutofox.seraj_hw.service.TaskService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing tasks and task occurrences.
 * Provides endpoints for scheduling tasks, listing tasks, and generating task reports.
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskOccurrenceService taskOccurrenceService;

    @Autowired
    private TaskReportService taskReportService;

    /**
     * Schedules a new task.
     *
     * @param task the task to be scheduled
     * @return ResponseEntity containing the scheduled task
     * @throws SchedulerException if there is an issue with the scheduler
     * @throws OverlappingTaskException if the task overlaps with an existing task
     */
    @PostMapping("/schedule")
    public ResponseEntity<Task> scheduleTask(@RequestBody Task task) throws SchedulerException, OverlappingTaskException {
        Task newTask = taskService.scheduleNewTask(task);
        return ResponseEntity.ok(newTask);
    }

    /**
     * Retrieves a list of tasks occurring after a specified start date.
     *
     * @param startDate the start date to filter tasks
     * @return ResponseEntity containing a list of task occurrences
     */
    @GetMapping("/list/byStartDate")
    public ResponseEntity<List<TaskOccurrence>> listTasks(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        List<TaskOccurrence> tasks = taskOccurrenceService.getTasksAfterStartTime(startDate);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves a list of tasks occurring within a specified period.
     *
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return ResponseEntity containing a list of task occurrences
     */
    @GetMapping("/list/byPeriod")
    public ResponseEntity<List<TaskOccurrence>> listTasksByPeriod(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TaskOccurrence> tasks = taskOccurrenceService.getTasksForPeriod(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves a list of task reports.
     *
     * @return ResponseEntity containing a list of task reports
     */
    @GetMapping("/reports")
    public ResponseEntity<List<TaskReportDTO>> getTaskReports() {
        List<TaskReportDTO> reports = taskReportService.getTaskReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Updates an existing task.
     *
     * @param taskId the ID of the task to be updated
     * @param request the task request containing updated information
     * @return ResponseEntity containing the updated task occurrence
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskOccurrence> editTask(@PathVariable Long taskId, @RequestBody TaskRequest request) {
        TaskOccurrence update = taskService.update(taskId, request);
        return ResponseEntity.ok(update);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param taskId the ID of the task to be deleted
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(Long.parseLong(taskId));
        return ResponseEntity.noContent().build();
    }
}
