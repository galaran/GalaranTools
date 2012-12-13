package me.galaran.bukkitutils.gtools;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoOrNotify {

    private static String getPlayerNotify = "&cNo player with name &3$1";
    private static String getPlayerFuzzyNotify = "&cNo player with name matching &3$1";
    private static String getWorldNotify = "&cWorld $1 not loaded";
    private static String isPlayerNotify = "&cThis command can be executed only by a player";
    private static String hasPermissionNotify = "&cYou don't have permission";

    public static void setNotifyMessages(String getPlayer, String getPlayerFuzzy,String getWorld,
                                         String isPlayer, String hasPermission) {
        getPlayerNotify = getPlayer;
        getPlayerFuzzyNotify = getPlayerFuzzy;
        getWorldNotify = getWorld;
        isPlayerNotify = isPlayer;
        hasPermissionNotify = hasPermission;
    }

    public static Player getPlayer(String name, CommandSender sender) {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            Messaging.send(sender, getPlayerNotify, name);
        }
        return player;
    }

    public static Player getPlayerFuzzy(String name, CommandSender sender) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            Messaging.send(sender, getPlayerFuzzyNotify, name);
        }
        return player;
    }

    public static World getWorld(String worldName, CommandSender sender) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Messaging.send(sender, getWorldNotify, worldName);
        }
        return world;
    }

    public static boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Messaging.send(sender, isPlayerNotify);
            return false;
        }
        return true;
    }

    public static boolean hasPermission(Player player, String perm, CommandSender sender) {
        boolean has = player.hasPermission(perm);
        if (!has) {
            Messaging.send(sender, hasPermissionNotify);
        }
        return has;
    }
}
