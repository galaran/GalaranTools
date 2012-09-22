package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.Command;
import com.sk89q.minecraft.util.commands.gtools.CommandContext;
import com.sk89q.minecraft.util.commands.gtools.CommandPermissions;
import me.galaran.bukkitutils.gtools.GUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;

public class GeneralCommands {

    private final GalaranTools plugin;

    public GeneralCommands(GalaranTools plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = { "hasperm" }, desc = "Check is Player has Permission",
            usage = "[player] <perm>", min = 1, max = 2)
    @CommandPermissions("gtools.main")
    public void hasPerm(CommandContext args, CommandSender sender) {
        if (args.argsLength() == 1) {
            if (sender instanceof Player) {
                printIsHasPerm(sender, (Player) sender, args.getString(0));
            } else {
                errorPlayerCommand(sender);
            }
        } else {
            Player target = getPlayerOrNotify(args.getString(0), false, sender);
            if (target != null) {
                printIsHasPerm(sender, target, args.getString(1));
            }
        }
    }

    private void printIsHasPerm(CommandSender sender, Player player, String perm) {
        String permString = player.hasPermission(perm) ? ChatColor.GREEN + "has" : ChatColor.RED + "has not";
        GUtils.sendMessage(sender, "Player " + ChatColor.GOLD + player.getName() + " " + permString + " permission " + perm);
    }

    @Command(aliases = { "chunk" }, desc = "Check is Chunk of world [x, z] loaded",
            usage = "<block_x> <block_z> [world]", min = 2, max = 3)
    @CommandPermissions("gtools.main")
    public void chunk(CommandContext args, CommandSender sender) {
        World world;
        if (args.argsLength() == 3) {
            world = getWorldOrNotify(args.getString(2), sender);
            if (world == null) return;
        } else {
            if (sender instanceof Player) {
                world = ((Player) sender).getWorld();
            } else {
                GUtils.sendMessage(sender, "Specify world as 3th parameter, when execute this command from the console");
                return;
            }
        }

        int x = args.getInteger(0);
        int z = args.getInteger(1);
        int cx = x >> 4;
        int cz = z >> 4;

        boolean chunkIsLoaded = world.isChunkLoaded(cx, cz);
        GUtils.sendMessage(sender, String.format("Chunk [%d %d] in the world §6%s§f is %s", cx, cz, world.getName(),
                chunkIsLoaded ? ChatColor.GREEN + "loaded" : ChatColor.RED + "not loaded"));
    }

    @Command(aliases = { "loc" }, desc = "Prints your location", min = 0, max = 0)
    @CommandPermissions("gtools.main")
    public void location(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            errorPlayerCommand(sender);
            return;
        }
        Player player = (Player) sender;
        GUtils.sendMessage(player, "Your location: " + player.getLocation().toString(), ChatColor.AQUA);
    }

    @Command(aliases = { "event" }, desc = "Prints event class handlers", usage = "<classname>", min = 1, max = 1)
    @CommandPermissions("gtools.main")
    public void event(CommandContext args, CommandSender sender) {
        Class eventClass;
        try {
            eventClass = Class.forName(args.getString(0));
        } catch (ClassNotFoundException e) {
            GUtils.sendMessage(sender, "Class " + args.getString(0) + " not loaded", ChatColor.RED);
            return;
        }

        if (!Event.class.isAssignableFrom(eventClass)) {
            GUtils.sendMessage(sender, "Class " + args.getString(0) + " is not a Event class", ChatColor.RED);
            return;
        }

        Field handlerField;
        try {
            handlerField = eventClass.getDeclaredField("handlers");
        } catch (NoSuchFieldException ex) {
            GUtils.sendMessage(sender, "Class " + args.getString(0) + " has no handler list", ChatColor.RED);
            return;
        }

        handlerField.setAccessible(true);

        HandlerList handlerList;
        try {
            handlerList = (HandlerList) handlerField.get(null);
        } catch (Exception ex) {
            GUtils.sendMessage(sender, ex.getMessage(), ChatColor.RED);
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "== " + ChatColor.DARK_PURPLE + eventClass.getName() + ChatColor.GRAY + " Handlers ==");
        for (RegisteredListener rl : handlerList.getRegisteredListeners()) {
            sender.sendMessage(String.format("§a%s§f - §6%s§f §3%s", rl.getPriority().name(), rl.getPlugin().getName(),
                    rl.getListener().getClass().getName()));
        }
    }

    private World getWorldOrNotify(String worldName, CommandSender sender) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            GUtils.sendMessage(sender, "World " + worldName + " not loaded", ChatColor.RED);
        }
        return world;
    }

    private Player getPlayerOrNotify(String name, boolean exact, CommandSender sender) {
        Player player = exact ? Bukkit.getPlayerExact(name) : Bukkit.getPlayer(name);
        if (player == null) {
            GUtils.sendMessage(sender, "Player " + (exact ? "" : "starting with ") + name + " not found", ChatColor.RED);
        }
        return player;
    }

    private void errorPlayerCommand(CommandSender sender) {
        GUtils.sendMessage(sender, "You should be a player to execute this command", ChatColor.RED);
    }
}
