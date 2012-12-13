package me.galaran.bukkitutils.gtools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Messaging {

    private static Logger log;
    private static String chatPrefix;

    public static void init(Logger logger, String chatPrefixx) {
        log = logger;
        chatPrefix = ChatColor.GRAY + "[" + chatPrefixx + "] " + ChatColor.WHITE;
    }

    public static void log(Level level, String message, Object... params) {
        String finalString = StringUtils.parameterizeString(message, params);
        log.log(level, ChatColor.stripColor(finalString));
    }

    public static void log(String message, Object... params) {
        log(Level.INFO, message, params);
    }

    public static void send(CommandSender sender, String message, Object... params) {
        String decorated = StringUtils.decorateString(message, params);
        if (!decorated.equals("$suppress")) {
            sender.sendMessage(chatPrefix + decorated);
        }
    }

    public static void sendPlayerIfOnline(String playerName, String message, Object... params) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            send(player, message, params);
        }
    }

    public static void broadcastServer(String message, Object... params) {
        String decorated = StringUtils.decorateString(message, params);
        if (!decorated.equals("$suppress")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say " + decorated);
        }
    }

    public static void broadcastLoc(Location loc, double radius, String message, Object... params) {
        for (Player curPlayer : Bukkit.getOnlinePlayers()) {
            Location curPlayerLoc = curPlayer.getLocation();
            if (!curPlayerLoc.getWorld().equals(loc.getWorld())) continue;
            if (curPlayerLoc.distance(loc) <= radius) {
                send(curPlayer, message, params);
            }
        }
    }

    public static String enDis(boolean state) {
        return state ? ChatColor.DARK_GREEN + "enabled" : ChatColor.DARK_RED + "disabled";
    }
}
