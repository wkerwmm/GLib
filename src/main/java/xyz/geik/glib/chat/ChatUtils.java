package xyz.geik.glib.chat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.geik.glib.GLib;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author hyperion, poyrazinan
 * @since 1.0
 */
public class ChatUtils {

    /**
     * Decimal formatter
     */
    public static final DecimalFormat FORMATTER = (DecimalFormat) NumberFormat.getNumberInstance();

    static {
        FORMATTER.setMinimumIntegerDigits(1);
        FORMATTER.setMaximumIntegerDigits(20);
        FORMATTER.setMaximumFractionDigits(2);
        FORMATTER.setGroupingSize(3);
    }

    /**
     * Hex pattern for color codes
     */
    private final static Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    /**
     * MiniMessage instance for parsing MiniMessage tags
     */
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Legacy serializer for converting & color codes
     */
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacy('&');

    /**
     * Applies chat color formats to message
     * @param message to convert
     * @return String of converted message
     */
    public static String color(String message) {
        message = StringEscapeUtils.unescapeHtml4(message);

        Component component = miniMessage.deserialize(message);

        String legacyMessage = legacySerializer.serialize(component);

        Matcher matcher = HEX_PATTERN.matcher(legacyMessage);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    /**
     * Applies chat color formats to message with PlaceholderAPI support
     * @param player OfflinePlayer for PlaceholderAPI
     * @param message to convert
     * @return String of converted message
     */
    public static String color(OfflinePlayer player, String message) {
        message = StringEscapeUtils.unescapeHtml4(message);

        message = PlaceholderAPI.setPlaceholders(player, message);

        Component component = miniMessage.deserialize(message);

        String legacyMessage = legacySerializer.serialize(component);

        Matcher matcher = HEX_PATTERN.matcher(legacyMessage);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    /**
     * Applies chat color formats to list
     * @param list to convert
     * @return List of converted message
     */
    public static List<String> color(List<String> list) {
        return list.stream().map(ChatUtils::color).collect(Collectors.toList());
    }

    /**
     * Applies chat color formats to list with PlaceholderAPI support
     * @param list to convert
     * @param player OfflinePlayer for PlaceholderAPI
     * @return List of converted message
     */
    public static List<String> color(List<String> list, OfflinePlayer player) {
        return list.stream().map(key -> color(player, key)).collect(Collectors.toList());
    }

    /**
     * Sends message to command sender
     * @param player executor
     * @param message to send
     */
    public static void sendMessage(@NotNull CommandSender player, String message) {
        player.sendMessage(ChatUtils.color(replacePlaceholders(message, new Placeholder("{prefix}",
                GLib.getPrefix()))));
    }

    /**
     * Sends message to command sender
     * @param player executor
     * @param message to send
     * @param placeholders to send
     */
    public static void sendMessage(@NotNull CommandSender player, String message, Placeholder... placeholders) {
        player.sendMessage(ChatUtils.color(
                replacePlaceholders(
                        replacePlaceholders(message, new Placeholder("{prefix}", GLib.getPrefix())),
                        placeholders)));
    }

    /**
     * Replaces placeholder data on string
     * <p><b>also format chat messages too @see ChatUtil#color(String)</b></p>
     *
     * @param string to be converted
     * @param placeholders additional placeholder data
     * @return converted string value
     */
    public static String replacePlaceholders(String string, Placeholder... placeholders) {
        for (Placeholder placeholder : placeholders) {
            string = string.replace(placeholder.getKey(), placeholder.getValue());
        }
        return color(string);
    }

    /**
     * Replaces placeholder data on list
     *
     * @param list to be converted
     * @param placeholders additional placeholder data
     * @return converted list value
     */
    public static List<String> replacePlaceholders(List<String> list, Placeholder... placeholders) {
        return list.stream().map(s -> replacePlaceholders(s, placeholders)).collect(Collectors.toList());
    }
}