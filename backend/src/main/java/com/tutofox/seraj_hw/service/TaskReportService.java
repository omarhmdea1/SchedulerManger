package com.tutofox.seraj_hw.service;

import com.tutofox.seraj_hw.dto.TaskReportDTO;
import com.tutofox.seraj_hw.entities.TaskReport;
import com.tutofox.seraj_hw.repository.TaskReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing task reports.
 * Provides functionality to log task events and retrieve task reports.
 */
@Service
public class TaskReportService {

    @Autowired
    private TaskReportRepository taskReportRepository;

    /**
     * Retrieves all task reports as a list of TaskReportDTO objects.
     *
     * @return a list of TaskReportDTO representing all task reports
     */
    public List<TaskReportDTO> getTaskReports() {
        List<TaskReport> tasks = taskReportRepository.findAll();

        return tasks.stream()
                .map(task -> new TaskReportDTO(
                        task.getTimestamp().toString(),
                        task.getMessage()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Logs a task event by creating a new TaskReport entry with the current timestamp and the provided message.
     *
     * @param message the message to be logged in the task report
     */
    public void logTaskEvent(String message) {
        TaskReport taskReport = new TaskReport();
        taskReport.setTimestamp(LocalDateTime.now());
        taskReport.setMessage(message);
        taskReportRepository.save(taskReport);
    }
}
