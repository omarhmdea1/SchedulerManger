package com.tutofox.seraj_hw.repository;

import com.tutofox.seraj_hw.entities.TaskOccurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskOccurrenceRepository extends JpaRepository<TaskOccurrence, Long> {

    @Query("SELECT o FROM TaskOccurrence o WHERE "
            + "o.startTime BETWEEN :startTime AND :endTime")
    List<TaskOccurrence> findOccurrencesInDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT o FROM TaskOccurrence o WHERE o.startTime > :startTime")
    List<TaskOccurrence> findOccurrencesAfterStartTime(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT t FROM TaskOccurrence t WHERE t.startTime < :newEndTime AND t.endTime > :newStartTime")
    List<TaskOccurrence> findOverlappingTasks(LocalDateTime newStartTime, LocalDateTime newEndTime);

    @Override
    Optional<TaskOccurrence> findById(Long taskId);
}
