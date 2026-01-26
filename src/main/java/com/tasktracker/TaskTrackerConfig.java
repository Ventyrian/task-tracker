package com.tasktracker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import java.awt.Color;

@ConfigGroup("tasktracker")
public interface TaskTrackerConfig extends Config
{
    @ConfigItem(
            keyName = "enableSfx",
            name = "Enable Sounds",
            description = "Play sound effects when Rolling, Backloging, and Completing tasks.",
            position = 1
    )
    default boolean enableSfx()
    {
        return true;
    }

    @ConfigItem(
            keyName = "enableBacklog",
            name = "Enable Backlog",
            description = "Show the backlog button and panel",
            position = 2
    )
    default boolean enableBacklog()
    {
        return true;
    }

    @ConfigItem(
            keyName = "removeActive",
            name = "Remove from Active",
            description = "Remove the current task from active once completed.",
            position = 3
    )
    default boolean removeActive()
    {
        return true;
    }

    @ConfigItem(
            keyName = "currentTaskHighlightColor",
            name = "Highlight Color",
            description = "The highlight color of the current task in the active tasks list.",
            position = 4
    )
    default Color currentTaskHighlightColor()
    {
        return Color.YELLOW;
    }

    @Getter
    @RequiredArgsConstructor
    enum TimestampFormat
    {
        // Format: (Pattern, Label shown in Dropdown)
        US("MM/dd/yyyy HH:mm", "US (12/31/2024)"),
        EU("dd/MM/yyyy HH:mm", "EU (31/12/2024)"),
        ISO("yyyy-MM-dd HH:mm", "ISO (2024-12-31)");

        private final String pattern;
        private final String label;

        @Override
        public String toString()
        {
            return label;
        }
    }

    @ConfigItem(
            keyName = "timestampFormat",
            name = "Date Format",
            description = "Choose how the completed date will be displayed.",
            position = 5
    )
    default TimestampFormat timestampFormat()
    {
        return TimestampFormat.US;
    }

    @Getter
    @RequiredArgsConstructor
    enum SortStyle
    {
        NEWEST("Newest First"),
        OLDEST("Oldest First");

        private final String name;

        // RuneLite uses toString() to determine what text shows in the dropdown
        @Override
        public String toString()
        {
            return name;
        }
    }

    @ConfigItem(
            keyName = "completedSortStyle",
            name = "Sort Order",
            description = "Choose how to sort the completed tasks list.",
            position = 6
    )
    default SortStyle completedSortStyle()
    {
        return SortStyle.OLDEST;
    }

    @Range(min = 0)
    @ConfigItem(
            keyName = "milestoneInterval",
            name = "Milestone Interval",
            description = "The number of completed tasks between highlights (e.g., 10). If set to 0 highlighting will be disabled.",
            position = 7
    )
    default int  milestoneInterval()
    {
        return 10;
    }

    @ConfigItem(
            keyName = "milestoneColor",
            name = "Milestone Color",
            description = "The color used to highlight the milestone.",
            position = 8
    )
    default Color  milestoneColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "allTasksJson",
            name = "All Tasks Json",
            description = "Json of all tasks currently saved in config",
            hidden = true,
            position = 30
    )
    default String allTasksJson()
    {
        return "";
    }

    @ConfigSection(
            name = "WARNING: IF YOU CLICK RESET THIS WILL ALSO CLEAR ALL TASK DATA!",
            description = "Only click rest after you have saved your task data in a safe location.",
            position = 9
    )
    String warningSection = "warningSection";
}
