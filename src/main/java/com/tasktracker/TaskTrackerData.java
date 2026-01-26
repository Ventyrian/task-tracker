package com.tasktracker;

import lombok.Data;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TaskTrackerData
{
    private String currentTask = "";
    private List<String> active = new ArrayList<>();
    private List<String> backlog = new ArrayList<>();
    private List<CompletedTask> completed = new ArrayList<>();
    private Set<String> repeatableTasks = new HashSet<>();

    // For existing users updating
    public Set<String> getRepeatableTasks()
    {
        if (repeatableTasks == null)
        {
            repeatableTasks = new HashSet<>();
        }
        return repeatableTasks;
    }
}

