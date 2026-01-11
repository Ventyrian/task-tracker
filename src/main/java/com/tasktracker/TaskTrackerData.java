package com.tasktracker;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskTrackerData
{
    private String currentTask = "";
    private List<String> active = new ArrayList<>();
    private List<String> backlog = new ArrayList<>();
    private List<CompletedTask> completed = new ArrayList<>();
}

