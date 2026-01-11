package com.tasklock;

import lombok.Data;

@Data
public class CompletedTask
{
    private final long completedAt;
    private final String task;

    public CompletedTask(String taskName)
    {
        this.task = taskName;
        this.completedAt = System.currentTimeMillis();
    }

    public CompletedTask( long completed, String taskName)
    {
        this.task = taskName;
        this.completedAt = completed;
    }

}
