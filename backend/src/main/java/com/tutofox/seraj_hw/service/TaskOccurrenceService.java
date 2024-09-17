package com.tutofox.seraj_hw.service;

import com.tutofox.seraj_hw.entities.TaskOccurrence;
import com.tutofox.seraj_hw.exception.InvalidDateRangeException;
import com.tutofox.seraj_hw.repository.TaskOccurrenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing task occurrences.
 * Provides functionality to check for task overlap, retrieve task occurrences, and save task occurrences.
 */
@Service
@Transactional
public class TaskOccurrenceService {

    @Autowired
    private TaskOccurrenceRepository taskOccurrenceRepository;

    /**
     * Checks if there is any task occurrence that overlaps with the specified time range.
     *
     * @param newTaskStartTime the start time of the new task
     * @param newTaskEndTime the end time of the new task
     * @return true if there is an overlapping task, false otherwise
     */
    public boolean checkForOverlap(LocalDateTime newTaskStartTime, LocalDateTime newTaskEndTime) {
        List<TaskOccurrence> overlappingTasks = taskOccurrenceRepository.findOverlappingTasks(newTaskStartTime, newTaskEndTime);
        return !overlappingTasks.isEmpty();
    }

    /**
     * Retrieves task occurrences that fall within the specified date range.
     *
     * @param start the start of the date range
     * @param end the end of the date range
     * @return a list of TaskOccurrence within the date range
     * @throws InvalidDateRangeException if the end date is before the start date
     */
    public List<TaskOccurrence> getTasksForPeriod(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new InvalidDateRangeException("End date cannot be before start date.");
        }
        return taskOccurrenceRepository.findOccurrencesInDateRange(start, end);
    }

    /**
     * Retrieves task occurrences that start after the specified start time.
     *
     * @param start the start time
     * @return a list of TaskOccurrence that start after the specified time
     */
    public List<TaskOccurrence> getTasksAfterStartTime(LocalDateTime start) {
        return taskOccurrenceRepository.findOccurrencesAfterStartTime(start);
    }

    /**
     * Saves a task occurrence to the repository.
     *
     * @param occurrence the TaskOccurrence to be saved
     * @return the saved TaskOccurrence
     */
    public TaskOccurrence save(TaskOccurrence occurrence) {
        return taskOccurrenceRepository.save(occurrence);
    }

    /**
     * Retrieves a task occurrence by its ID.
     *
     * @param taskId the ID of the task occurrence
     * @return an Optional containing the TaskOccurrence if found, otherwise empty
     */
    public Optional<TaskOccurrence> findTaskById(Long taskId) {
        return taskOccurrenceRepository.findById(taskId);
    }

    /**
     * Checks if a task occurrence with the specified ID exists.
     *
     * @param taskId the ID of the task occurrence
     * @return true if a task occurrence with the specified ID exists, false otherwise
     */
    public boolean existsById(Long taskId) {
        return taskOccurrenceRepository.existsById(taskId);
    }
}
