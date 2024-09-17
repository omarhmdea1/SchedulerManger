package com.tutofox.seraj_hw.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequest {

    private long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration;
    private int repeatEvery;

}