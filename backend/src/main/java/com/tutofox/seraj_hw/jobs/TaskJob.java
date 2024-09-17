package com.tutofox.seraj_hw.jobs;

import com.tutofox.seraj_hw.entities.Task;
import com.tutofox.seraj_hw.service.TaskReportService;
import com.tutofox.seraj_hw.service.TaskService;
import com.tutofox.seraj_hw.utils.Converter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Quartz job for executing tasks.
 * This job retrieves a task from the task service, logs the start and end events,
 * and simulates the task's work by sleeping for the task's duration.
 */
public class TaskJob implements Job {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskReportService taskReportService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long taskId = (Long) context.getJobDetail().getJobDataMap().get("taskId");
        Task task = taskService.findTaskById(taskId).orElseThrow();

        System.out.println("Task " + taskId + " started.");
        taskReportService.logTaskEvent("Task " + taskId + " started");

        try {
            Thread.sleep(Converter.minutesToMilliseconds(task.getDuration()));  // Simulating task's work
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            taskReportService.logTaskEvent("Task " + taskId + " ended");
            System.out.println("Task " + taskId + " ended.");
        }

    }
}
