package com.tasklock;

import com.google.gson.Gson;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@PluginDescriptor(
	name = "Task Lock"
)
public class TaskLockPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TaskLockConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ConfigManager configManager;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private Gson gson;

    private TaskLockPanel panel;
    private NavigationButton navButton;

    private final ExecutorService audioExecutor = Executors.newSingleThreadExecutor();



	@Override
	protected void startUp() throws Exception
	{
		log.debug("Task Lock started!");

        BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/com/tasklock/icon.png");

        SwingUtilities.invokeLater( () -> {
            panel = new TaskLockPanel(this, configManager, gson, spriteManager);
            panel.setupSections();

            navButton = NavigationButton.builder()
                    .tooltip("Task Lock")
                    .icon(icon)
                    .panel(panel)
                    .build();

            clientToolbar.addNavigation(navButton);

        });
	}

	@Override
	protected void shutDown() throws Exception
	{
        clientToolbar.removeNavigation(navButton);
        audioExecutor.shutdown();
		log.debug("Task Lock stopped!");
	}

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        // Check if the change belongs to your plugin group
        if (event.getGroup().equals("tasklock"))
        {
            panel.refresh();
        }
    }

	@Provides
	TaskLockConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TaskLockConfig.class);
	}

    // Function to play custom sounds from resources folder
    public void playSound(String soundFile)
    {
        // Check config
        if (!config.enableSfx())
        {
            return;
        }

        audioExecutor.submit( () ->
        {
            // Load the file from resources
            try (InputStream rawStream = getClass().getResourceAsStream(soundFile);)
            {
                if (rawStream == null)
                {
                    return;
                }

                // Buffer the stream to prevent exceptions (This is raw data)
                InputStream bufferedSteam = new BufferedInputStream(rawStream);
                // Translate to an audioStream rawData -> audioData
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedSteam);

                // Get the clip (create small audio player)
                Clip clip = AudioSystem.getClip();

                // Add a listener to close the clip when done
                clip.addLineListener(event ->
                {
                    if (event.getType() == LineEvent.Type.STOP)
                    {
                        clip.close();
                    }
                });

                // Load the audio
                clip.open(audioStream);

                // Optional: Reduce volume slightly if it's too loud
                // FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // gainControl.setValue(-10.0f); // Reduce by 10 decibels

                // Play the audio
                log.debug("Playing sound " +  soundFile);
                clip.start();
            }
            catch (Exception e)
            {
                log.error("Failed to play sound: " + soundFile, e);
            }
        });
    }

    // Helper function to get the current config color
    public Color getCurrentTaskBorderColor()
    {
        return config.currentTaskBorderColor();
    }

    // Helper function to get the current dateTimeFormat
    public SimpleDateFormat getDateTimeFormat()
    {
        return new SimpleDateFormat(config.timestampFormat().getPattern());
    }

    // Helper function to get the current timestamp format as a string
    public String getTimestampFormat()
    {
        return config.timestampFormat().getPattern();
    }
}
