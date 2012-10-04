package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.Command;
import com.sk89q.minecraft.util.commands.gtools.CommandContext;
import com.sk89q.minecraft.util.commands.gtools.CommandPermissions;
import me.galaran.bukkitutils.gtools.DoOrNotify;
import me.galaran.bukkitutils.gtools.GUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
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
            if (DoOrNotify.isPlayer(sender)) {
                printIsHasPerm(sender, (Player) sender, args.getString(0));
            }
        } else {
            Player target = DoOrNotify.getPlayer(args.getString(0), false, sender);
            if (target != null) {
                printIsHasPerm(sender, target, args.getString(1));
            }
        }
    }

    private void printIsHasPerm(CommandSender sender, Player player, String perm) {
        GUtils.sendMessage(sender, "Player &6$1 $2 permission $3", player.getName(),
                (player.hasPermission(perm) ? ChatColor.GREEN + "has" : ChatColor.RED + "has not"), perm);
    }

    @Command(aliases = { "chunk" }, desc = "Check is Chunk of world [x, z] loaded",
            usage = "<block_x> <block_z> [world]", min = 2, max = 3)
    @CommandPermissions("gtools.main")
    public void chunk(CommandContext args, CommandSender sender) {
        World world;
        if (args.argsLength() == 3) {
            world = DoOrNotify.getWorld(args.getString(2), sender);
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
        GUtils.sendMessage(sender, "Chunk [$1 $2] in the world &6$3&f is $4", cx, cz, world.getName(),
                chunkIsLoaded ? ChatColor.GREEN + "loaded" : ChatColor.RED + "not loaded");
    }

    @Command(aliases = { "loc" }, desc = "Prints your location", min = 0, max = 0)
    @CommandPermissions("gtools.main")
    public void location(CommandContext args, CommandSender sender) {
        if (!DoOrNotify.isPlayer(sender)) return;
        Player player = (Player) sender;

        GUtils.sendMessage(player, "Your location: " + ChatColor.YELLOW + GUtils.locToString(player.getLocation()));
    }

    @Command(aliases = { "tp" }, desc = "Teleport player to location", min = 7, max = 7,
             usage = "<player> <world> <x> <y> <z> <pitch> <yaw>")
    @CommandPermissions("gtools.main")
    public void tp(CommandContext args, CommandSender sender) {
        Player player = DoOrNotify.getPlayer(args.getString(0), true, sender);
        if (player == null) return;

        World world = DoOrNotify.getWorld(args.getString(1), sender);
        if (world == null) return;

        Location tpLoc = new Location(world, args.getDouble(2), args.getDouble(3), args.getDouble(4),
                (float) args.getDouble(6), (float) args.getDouble(5));
        player.teleport(tpLoc);
    }

    @Command(aliases = { "event" }, desc = "Prints event class handlers", usage = "<classname>", min = 1, max = 1)
    @CommandPermissions("gtools.main")
    public void event(CommandContext args, CommandSender sender) {
        Class eventClass;
        try {
            eventClass = Class.forName(args.getString(0));
        } catch (ClassNotFoundException e) {
            GUtils.sendMessage(sender, "&cClass $1 not loaded", args.getString(0));
            return;
        }

        if (!Event.class.isAssignableFrom(eventClass)) {
            GUtils.sendMessage(sender, "&cClass $1 is not a Event class", args.getString(0));
            return;
        }

        Field handlerField;
        try {
            handlerField = eventClass.getDeclaredField("handlers");
        } catch (NoSuchFieldException ex) {
            GUtils.sendMessage(sender, "&cClass $1 has no handler list", args.getString(0));
            return;
        }

        handlerField.setAccessible(true);

        HandlerList handlerList;
        try {
            handlerList = (HandlerList) handlerField.get(null);
        } catch (Exception ex) {
            GUtils.sendMessage(sender, ChatColor.RED + ex.getMessage());
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "== " + ChatColor.DARK_PURPLE + eventClass.getName() + ChatColor.GRAY + " handlers ==");
        for (RegisteredListener rl : handlerList.getRegisteredListeners()) {
            sender.sendMessage(String.format("§a%s§f - §6%s§f §3%s", rl.getPriority().name(), rl.getPlugin().getName(),
                    rl.getListener().getClass().getName()));
        }
    }

    @Command(aliases = { "stackdata" }, desc = "Shows data of item stack in your hand", min = 0, max = 0)
    @CommandPermissions("gtools.main")
    public void stackdata(CommandContext args, CommandSender sender) {
        if (!DoOrNotify.isPlayer(sender)) return;
        Player player = (Player) sender;

        ItemStack stack = player.getItemInHand();
        String message;
        if (stack == null || stack.getType() == Material.AIR) {
            message = ChatColor.RED + "No stack in hand";
        } else {
            message = "Data: " + ChatColor.GREEN + stack.getData().getData();
        }
        GUtils.sendMessage(player, message);
    }
}
