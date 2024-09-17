package com.tutofox.seraj_hw.service;

import com.tutofox.seraj_hw.dto.TaskRequest;
import com.tutofox.seraj_hw.entities.Task;
import com.tutofox.seraj_hw.entities.TaskOccurrence;
import com.tutofox.seraj_hw.exception.TaskDeletionFailureException;
import com.tutofox.seraj_hw.exception.TaskUpdateFailureException;
import com.tutofox.seraj_hw.exception.OverlappingTaskException;
import com.tutofox.seraj_hw.jobs.TaskJob;
import com.tutofox.seraj_hw.repository.TaskRepository;
import com.tutofox.seraj_hw.utils.Converter;
import jakarta.transaction.Transactional;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing tasks.
 * Provides functionality for scheduling, updating, deleting, and managing tasks and their occurrences.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskOccurrenceService taskOccurrenceService;

    /**
     * Finds a task by its ID.
     *
     * @param taskId the ID of the task
     * @return an Optional containing the task if found, otherwise empty
     */
    public Optional<Task> findTaskById(long taskId) {
        return taskRepository.findTaskById(taskId);
    }

    /**
     * Updates an existing task occurrence with new details.
     *
     * @param taskId the ID of the task occurrence to update
     * @param taskRequest the new details for the task
     * @return the updated TaskOccurrence
     * @throws TaskUpdateFailureException if the task is not found
     */
    public TaskOccurrence update(Long taskId, TaskRequest taskRequest) {
        Optional<TaskOccurrence> optionalTask = taskOccurrenceService.findTaskById(taskId);

        if (optionalTask.isPresent()) {
            TaskOccurrence task = optionalTask.get();
            task.setName(taskRequest.getName());
            task.setStartTime(taskRequest.getStartTime());
            task.setEndTime(taskRequest.getEndTime());
            task.setDuration(taskRequest.getDuration());

            return taskOccurrenceService.save(task);
        }
        throw new TaskUpdateFailureException("Failed to update task: Task not found.");
    }

    /**
     * Schedules a new task and its occurrences.
     *
     * @param task the task to schedule
     * @return the saved task with scheduled occurrences
     * @throws OverlappingTaskException if the task overlaps with an existing task
     * @throws SchedulerException if an error occurs while scheduling
     */
    public Task scheduleNewTask(Task task) throws OverlappingTaskException, SchedulerException {
        Task savedTask = taskRepository.save(task);
        List<TaskOccurrence> occurrences = generateOccurrences(savedTask);
        savedTask.setOccurrences(occurrences);
        savedTask = taskRepository.save(savedTask);

        scheduleOccurrences(savedTask.getOccurrences());
        return savedTask;
    }

    /**
     * Reschedules missed tasks when the application is ready.
     * This method is triggered by the ApplicationReadyEvent.
     *
     * @throws SchedulerException if an error occurs while rescheduling
     */
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleMissedTasks() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<TaskOccurrence> tasks = taskOccurrenceService.getTasksAfterStartTime(now);

            for (TaskOccurrence taskOccurrence : tasks) {
                rescheduleJob(taskOccurrence);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while rescheduling jobs", e);
        }
    }

    /**
     * Reschedules a job for a given task occurrence.
     *
     * @param task the task occurrence to reschedule
     * @throws SchedulerException if an error occurs while rescheduling
     */
    private void rescheduleJob(TaskOccurrence task) throws SchedulerException {
        JobKey jobKey = new JobKey("task-" + task.getId());

        if (scheduler.checkExists(jobKey)) {
            TriggerKey triggerKey = new TriggerKey("trigger-" + task.getId());

            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startAt(Date.from(task.getStartTime().atZone(ZoneId.systemDefault()).toInstant()))
                    .endAt(Date.from(task.getEndTime().atZone(ZoneId.systemDefault()).toInstant()))
                    .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);
            System.out.println("Job rescheduled for task: " + task.getId());
        } else {
            System.out.println("No job found with the key: " + jobKey);
        }
    }

    /**
     * Generates task occurrences based on the task details and repetition settings.
     *
     * @param task the task for which occurrences are to be generated
     * @return a list of generated TaskOccurrence objects
     * @throws OverlappingTaskException if any occurrence overlaps with an existing task
     */
    private List<TaskOccurrence> generateOccurrences(Task task) throws OverlappingTaskException {
        List<TaskOccurrence> occurrences = new ArrayList<>();
        LocalDateTime currentStart = task.getStartTime();
        LocalDateTime currentEnd = task.getStartTime().plusMinutes(task.getDuration());

        while (!currentStart.isAfter(task.getEndTime())) {

            if(taskOccurrenceService.checkForOverlap(currentStart, currentEnd)) {
                taskRepository.delete(task);
                throw new OverlappingTaskException("Task overlaps with existing task.");
            }

            TaskOccurrence occurrence = new TaskOccurrence();
            occurrence.setName(task.getName());
            occurrence.setStartTime(currentStart);
            occurrence.setEndTime(currentEnd);
            occurrence.setDuration(task.getDuration());
            occurrence.setTask(task);
            occurrences.add(occurrence);

            currentStart = currentStart.plusDays(task.getRepeatEvery());
            currentEnd = currentStart.plusMinutes(task.getDuration());
        }

        return occurrences;
    }

    /**
     * Schedules occurrences of tasks with Quartz.
     *
     * @param occurrences the list of TaskOccurrence objects to schedule
     * @throws SchedulerException if an error occurs while scheduling
     */
    private void scheduleOccurrences(List<TaskOccurrence> occurrences) throws SchedulerException {
        for (TaskOccurrence occurrence : occurrences) {
            JobDetail jobDetail = createJobDetail(occurrence);
            Trigger trigger = createTrigger(occurrence);

            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.deleteJob(jobDetail.getKey());
            }

            if (scheduler.checkExists(trigger.getKey())) {
                scheduler.deleteJob(jobDetail.getKey());
            }

            scheduler.scheduleJob(jobDetail, trigger);

            System.out.println("Create job with key " + jobDetail.getKey());
        }
    }

    /**
     * Creates a JobDetail for the given task occurrence.
     *
     * @param occurrence the task occurrence
     * @return a JobDetail instance
     */
    private JobDetail createJobDetail(TaskOccurrence occurrence) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskId", occurrence.getId());

        return JobBuilder.newJob(TaskJob.class)
                .withIdentity("task-" + occurrence.getId())
                .storeDurably()
                .usingJobData(jobDataMap)
                .build();
    }

    /**
     * Creates a Trigger for the given task occurrence.
     *
     * @param occurrence the task occurrence
     * @return a Trigger instance
     */
    private Trigger createTrigger(TaskOccurrence occurrence) {
        return TriggerBuilder.newTrigger()
                .withIdentity("trigger-" + occurrence.getId())
                .startAt(Date.from(occurrence.getStartTime().atZone(ZoneId.systemDefault()).toInstant()))
                .endAt(Date.from(occurrence.getEndTime().atZone(ZoneId.systemDefault()).toInstant()))
                .build();
    }

    /**
     * Deletes a task by its ID.
     *
     * @param taskId the ID of the task to delete
     * @return true if the task was deleted successfully, false otherwise
     * @throws TaskDeletionFailureException if the task cannot be found or deleted
     */
    public boolean deleteTask(Long taskId) {
        Optional<TaskOccurrence> task = taskOccurrenceService.findTaskById(taskId);
        if (task.isPresent()) {
            taskRepository.deleteById(task.get().getTask().getId());
            return true;
        }
        throw new TaskDeletionFailureException("Task not found or cannot be deleted.");
    }
}
