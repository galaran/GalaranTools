package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.Command;
import com.sk89q.minecraft.util.commands.gtools.CommandContext;
import com.sk89q.minecraft.util.commands.gtools.CommandPermissions;
import me.galaran.bukkitutils.gtools.IOUtils;
import me.galaran.bukkitutils.gtools.ItemUtils;
import me.galaran.bukkitutils.gtools.LocUtils;
import me.galaran.bukkitutils.gtools.text.Messaging;
import me.galaran.bukkitutils.gtools.text.StringUtils;
import org.bukkit.*;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

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
            if (Messaging.isPlayer(sender)) {
                printIsHasPerm(sender, (Player) sender, args.getString(0));
            }
        } else {
            Player target = Messaging.getPlayerFuzzy(args.getString(0), sender);
            if (target != null) {
                printIsHasPerm(sender, target, args.getString(1));
            }
        }
    }

    private void printIsHasPerm(CommandSender sender, Player player, String perm) {
        if (player.hasPermission(perm)) {
            Messaging.send(sender, "player-has-perm", player.getName(), perm);
        } else {
            Messaging.send(sender, "player-hasnt-perm", player.getName(), perm);
        }
    }

    @Command(aliases = { "chunk" }, desc = "Check is Chunk of world [x, z] loaded",
            usage = "<block_x> <block_z> [world]", min = 2, max = 3)
    @CommandPermissions("gtools.main")
    public void chunk(CommandContext args, CommandSender sender) {
        World world;
        if (args.argsLength() == 3) {
            world = Messaging.getWorld(args.getString(2), sender);
            if (world == null) return;
        } else {
            if (sender instanceof Player) {
                world = ((Player) sender).getWorld();
            } else {
                Messaging.send(sender, "chunk-specify-world");
                return;
            }
        }

        int x = args.getInteger(0);
        int z = args.getInteger(1);
        int cx = x >> 4;
        int cz = z >> 4;

        Messaging.send(sender, world.isChunkLoaded(cx, cz) ? "chunk-loaded" : "chunk-not-loaded", cx, cz, world.getName());
    }

    @Command(aliases = { "loc" }, desc = "Prints your location", min = 0, max = 0)
    @CommandPermissions("gtools.main")
    public void location(CommandContext args, CommandSender sender) {
        if (!Messaging.isPlayer(sender)) return;
        Player player = (Player) sender;

        Messaging.send(player, "current-loc", LocUtils.toStringFull(player.getLocation()));
    }

    @Command(aliases = { "tp" }, desc = "Teleport player to location", min = 5, max = 7,
             usage = "<player> <world> <x> <y> <z> [pitch] [yaw]")
    @CommandPermissions("gtools.main")
    public void tp(CommandContext args, CommandSender sender) {
        Player player = Messaging.getPlayer(args.getString(0), sender);
        if (player == null) return;

        World world = Messaging.getWorld(args.getString(1), sender);
        if (world == null) return;

        Location tpLoc = new Location(world, args.getDouble(2), args.getDouble(3), args.getDouble(4));
        if (args.argsLength() >= 6) {
            tpLoc.setPitch((float) args.getDouble(5));
        }
        if (args.argsLength() == 7) {
            tpLoc.setYaw((float) args.getDouble(6));
        }
        
        player.teleport(tpLoc);
    }

    @Command(aliases = { "event" }, desc = "Prints event class handlers", usage = "<classname>", min = 1, max = 1)
    @CommandPermissions("gtools.main")
    public void event(CommandContext args, CommandSender sender) {
        String className = args.getString(0);
        Class eventClass;
        try {
            eventClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Messaging.send(sender, "event-no-class", className);
            return;
        }

        if (!Event.class.isAssignableFrom(eventClass)) {
            Messaging.send(sender, "event-not-an-event-class", className);
            return;
        }

        Field handlerField;
        try {
            handlerField = eventClass.getDeclaredField("handlers");
        } catch (NoSuchFieldException ex) {
            Messaging.send(sender, "event-no-handlers", className);
            return;
        }

        handlerField.setAccessible(true);

        HandlerList handlerList;
        try {
            handlerList = (HandlerList) handlerField.get(null);
        } catch (Exception ex) {
            Messaging.sendRaw(sender, ChatColor.RED + ex.getMessage());
            return;
        }

        Messaging.sendNoPrefix(sender, "event-header", eventClass.getName());
        for (RegisteredListener rl : handlerList.getRegisteredListeners()) {
            Messaging.sendNoPrefix(sender, "event-entry", rl.getPriority().name(), rl.getPlugin().getName(),
                    rl.getListener().getClass().getName());
        }
    }

    @Command(aliases = { "id" }, desc = "Shows id and data of the stack in your hand", min = 0, max = 0)
    @CommandPermissions("gtools.main")
    public void stackInfo(CommandContext args, CommandSender sender) {
        if (!Messaging.isPlayer(sender)) return;
        Player player = (Player) sender;

        ItemStack stack = player.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            Messaging.send(player, "no-stack-in-hand");
        } else {
            Messaging.sendRaw(player, "&e$1&7:&a$2", stack.getTypeId(), stack.getDurability());
        }
    }

    @Command(aliases = { "addlore" }, desc = "Add lore line to stack in hand", usage = "<lore_line>", min = 1)
    @CommandPermissions("gtools.main")
    public void addLore(CommandContext args, CommandSender sender) {
        if (!Messaging.isPlayer(sender)) return;
        Player player = (Player) sender;

        ItemStack stack = player.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            Messaging.send(player, "no-stack-in-hand");
        } else {
            ItemUtils.addLore(stack, StringUtils.colorizeAmps(args.getJoinedStrings(0)));
        }
    }

    @Command(aliases = { "dumpchunks" }, desc = "Save chunk dump of specified world",
            usage = "<world>", min = 1, max = 1)
    @CommandPermissions("gtools.main")
    public void dumpChunks(CommandContext args, CommandSender sender) {
        World world = Messaging.getWorld(args.getString(0), sender);
        if (world == null) return;
        
        Chunk[] chunks = world.getLoadedChunks();
        Arrays.sort(chunks, new Comparator<Chunk>() {
            @Override
            public int compare(Chunk c1, Chunk c2) {
                int zDist = c1.getZ() - c2.getZ();
                return zDist == 0 ? c1.getX() - c2.getX() : zDist;
            }
        });

        File file = IOUtils.firstAvailableNumberedFile(new File(plugin.getDataFolder(), "chunk_dumps"), world.getName() + ".txt");
        try {
            PrintStream ps = new PrintStream(file);
            for (Chunk chunk : chunks) {
                ps.printf(Locale.US, "[%4d %4d] e:%2d, te:%2d\n", chunk.getX(), chunk.getZ(), chunk.getEntities().length, chunk.getTileEntities().length);
            }
            ps.close();
            Messaging.send(sender, "chunks-dump.ok", file.getName());
        } catch (FileNotFoundException ex) {
            throw new CommandException("Unable to get file for chunk dump", ex);
        }
    }
}
