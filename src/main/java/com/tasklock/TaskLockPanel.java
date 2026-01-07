package com.tasklock;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Random;


public class TaskLockPanel extends PluginPanel
{
    private final JPanel currentTaskPanel = new JPanel();
    private final JLabel currentTaskLabel = new JLabel("No Current Task");
    private final JButton rollTask = new JButton("Roll Task");
    private final JLabel activeHeader = new JLabel("Active Tasks");
    private final JPanel activeListPanel =  new JPanel();
    private final JButton activeTaskButton = new JButton("Edit");
    private final JLabel backlogHeader =  new JLabel("Backlog");
    private final JPanel backlogListPanel =   new JPanel();
    private final JButton backlogButton =  new JButton("Edit");
    private final JLabel completedHeader =  new JLabel("Completed Tasks");
    private final JPanel completedListPanel = new JPanel();
    private final JButton completedTaskButton =  new JButton("Details");
    private final Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
    private final Border line = BorderFactory.createLineBorder(Color.WHITE);
    private final Border compoundBorder = BorderFactory.createCompoundBorder(margin, BorderFactory.createCompoundBorder(line, margin));
    private final ConfigManager configManager;

    public TaskLockPanel(ConfigManager configManager)
    {
        super();
        this.configManager = configManager;

        setLayout(new GridBagLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

    }

    public void setupSections()
    {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0; // Row counter
        c.insets = new Insets(0, 0, 10, 0); // Bottom margin for spacing

        // SECTION 1: Current Task
        addSection(this, c, new JLabel("Current Task"), currentTaskPanel, rollTask, "currentTask");

        // SECTION 2: Active Tasks
        addSection(this, c, activeHeader, activeListPanel, activeTaskButton, "activeTasks");

        // SECTION 3: Backlog
        addSection(this, c, backlogHeader, backlogListPanel, backlogButton, "backlog");

        // SECTION 4: Completed Tasks
        addSection(this, c, completedHeader, completedListPanel, completedTaskButton, "completedTasks");

        revalidate();
        repaint();
    }

    private void addSection(JPanel parent, GridBagConstraints c, JLabel header, JPanel panel, JButton button, String keyName)
    {
        // Convert camelCase string to two words capitalized and reset baseHeader
        String headerName = keyName.replaceAll("([a-z])([A-Z])", "$1 $2");
        String baseHeader = headerName.substring(0, 1).toUpperCase() + headerName.substring(1);
        header.setText(baseHeader);

        // Get content from config and convert CSV into List
        List<String> contentList = getTasksByKey(keyName);

        // Set Header Text
        if(!contentList.isEmpty() && !contentList.get(0).isEmpty() && !header.getText().equals("Current Task"))
        {
            header.setText(header.getText() + " (" + contentList.size() + ")");
        }

        // Add Header (Left Aligned)
        header.setFont(FontManager.getRunescapeBoldFont());
        c.anchor = GridBagConstraints.WEST;
        parent.add(header, c);
        c.gridy++;

        // Add Content Text (Left Aligned)
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(line, margin));

        JLabel taskLabel = new JLabel();

        if (keyName.equals("currentTask"))
        {
            currentTaskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(currentTaskLabel);
        }
        else
        {
            // If list is empty show the no tasks label
            if (contentList.isEmpty() || contentList.get(0).isEmpty())
            {
                panel.add(new JLabel("No " + header.getText()));
            }
            // If list is not empty, add each task to the panel
            else
            {
                for (String task : contentList)
                {
                    // Parse the date out of the string if completedTasks
                    if(keyName.equals("completedTasks"))
                    {
                        String[] split = task.split("!");
                        if (split.length == 2 )
                        {
                            taskLabel = new JLabel();
                            taskLabel.setText("• " + task.split("!")[1]);
                        }
                    }
                    else
                    {
                        taskLabel = new JLabel();
                        taskLabel.setText("• " + task);
                        if (task.equals(currentTaskLabel.getText()) && keyName.equals("activeTasks"))
                        {
                            taskLabel.setForeground(Color.BLACK);
                            taskLabel.setBackground(Color.GREEN);
                            taskLabel.setOpaque(true);
                        }
                    }

                    taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panel.add(taskLabel);

                }
            }
        }

        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 5, 0); // Tighten gap between text and button
        parent.add(panel, c);
        c.gridy++;

        // Add Button (Centered)

        // The Secret: Change anchor to CENTER and fill to NONE
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 20, 0); // Large margin at bottom of section

        // Add specific event listener
        switch(keyName)
        {
            case "currentTask":
                button.addActionListener(e -> rollTask());
                break;
            case "activeTasks":
                break;
            case "backlog":
                break;
            case "completedTasks":
                break;
            default:
                break;
        }

        parent.add(button, c);
        c.gridy++;

        // Reset fill for next section's labels
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 10, 0);

    }

    public void refresh()
    {
        // Use SwingUtilities to ensure UI changes happen on the correct thread
        SwingUtilities.invokeLater(() -> {
            this.removeAll(); // Clear the entire panel
            setupSections();  // Re-run your logic to add headers, lists, and buttons
            this.revalidate();
            this.repaint();
        });
    }

    private List<String> getTasksByKey(String keyName)
    {
        String content = configManager.getConfiguration("tasklock",keyName);
        content = content == null ? "" : content;
        return List.of(content.split(","));
    }

    private void rollTask()
    {
        List<String> activeTasks = getTasksByKey("activeTasks");
        Random random = new Random();
        currentTaskLabel.setText((activeTasks.get(random.nextInt(activeTasks.size()))));
        this.refresh();
    }

    private JLabel createHeader(String title)
    {
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBorder(margin);
        label.setBackground(ColorScheme.DARK_GRAY_COLOR);
        return label;
    }

    private JPanel createListPanel()
    {
        JPanel listPanel = new JPanel();
        listPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        return listPanel;
    }

    private JScrollPane wrapScrollable(JPanel listPanel)
    {
        JScrollPane scrollPane = new JScrollPane(listPanel);

        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setBorder(compoundBorder);

        // This controls visible height
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        return scrollPane;
    }

    private JButton createButton(String title)
    {
        JButton button = new JButton(title);
        button.setBackground(ColorScheme.DARK_GRAY_COLOR);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        return button;
    }

//    public void setCurrentTask(String task)
//    {
//        if (task.isEmpty() || task == null)
//        {
//            currentTask.setText("No current task");
//        }
//        else
//        {
//            currentTask.setText(task);
//        }
//    }
//
//    public void setActiveTasks()
//    {
//        activeListPanel.removeAll();
//
//        String configString = configManager.getConfiguration("tasklock","activeTasks");
//        configString = configString == null ? "" : configString;
//        List<String> tasks = new ArrayList<>(List.of(configString.split(",")));
//
//        if(configString.isEmpty())
//        {
//            tasks.clear();
//        }
//
//        if(tasks.isEmpty())
//        {
//            activeListPanel.add(new JLabel("No active tasks"));
//        }
//        else
//        {
//            for( String task : tasks)
//            {
//                activeListPanel.add(new JLabel("• " + task));
//            }
//            activeHeader.setText("Active Tasks (" + (tasks.size()) + ")");
//        }
//
//        revalidate();
//        repaint();
//    }
//
//    public void setBacklogTasks()
//    {
//        backlogListPanel.removeAll();
//
//        String configString = configManager.getConfiguration("tasklock","backlogTasks");
//        configString = configString == null ? "" : configString;
//        List<String> tasks = new ArrayList<>(List.of(configString.split(",")));
//
//        if(configString.isEmpty())
//        {
//            tasks.clear();
//        }
//
//        if(tasks.isEmpty())
//        {
//            backlogListPanel.add(new JLabel("No backlogged tasks"));
//        }
//        else
//        {
//            for( String task : tasks)
//            {
//                backlogListPanel.add(new JLabel("• " + task));
//            }
//            backlogHeader.setText("Backlog (" + (tasks.size()) + ")");
//        }
//
//        revalidate();
//        repaint();
//    }
//
//
//    public void setCompletedTasks()
//    {
//        completedTable.removeAll();
//
//        String configString = configManager.getConfiguration("tasklock","completedTasks");
//        configString = configString == null ? "" : configString;
//        List<String> tasks = new ArrayList<>(List.of(configString.split(",")));
//
//        if(configString.isEmpty())
//        {
//            tasks.clear();
//        }
//
//        if (tasks.isEmpty())
//        {
//            completedTable.add(new JLabel("No completed tasks"));
//        }
//        else
//        {
//            for (String task : tasks)
//            {
//                String[] split = task.split("\\+");
//                if (split.length == 2)
//                {
//                    String date =  split[0];
//                    String name =  split[1];
//                    completedTable.add(new JLabel("• " + name));
//                }
//
//            }
//            completedHeader.setText("Completed Tasks (" + (tasks.size()) + ")");
//        }
//
//        revalidate();
//        repaint();
//
//    }

}
