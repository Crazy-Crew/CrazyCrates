package us.crazycrew.crazycrates.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.utils.AdventureUtils;
import us.crazycrew.crazycrates.ApiLoader;
import us.crazycrew.crazycrates.configurations.PluginSettings;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Description: Sends messages to player or console.
 */
public class MessageSender {

    /**
     * Sends a message to anything.
     *
     * @param audience the recipient
     * @param component what gets converted to a component
     */
    public static void send(Audience audience, String component) {
        String messagePrefix = ApiLoader.getPluginConfig().getProperty(PluginSettings.COMMAND_PREFIX);
        boolean prefix = ApiLoader.getPluginConfig().getProperty(PluginSettings.COMMAND_PREFIX_TOGGLE);


        audience.sendMessage(prefix ? AdventureUtils.parse(messagePrefix).append(Component.text(component)) : Component.text(component));
    }

    /**
     * Sends a log message
     *
     * @param component what gets converted to a component
     */
    public static void send(String component) {
        boolean isLogging = ApiLoader.getPluginConfig().getProperty(PluginSettings.VERBOSE_LOGGING);

        if (isLogging) CrazyLogger.info(component);
    }
}