package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.Command;
import com.sk89q.minecraft.util.commands.gtools.CommandContext;
import com.sk89q.minecraft.util.commands.gtools.CommandPermissions;
import me.galaran.bukkitutils.gtools.text.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCmdCommand {

    public PlayerCmdCommand(GalaranTools plugin) { }

    @Command(aliases = { "playercmd", "pcmd" }, desc = "Execute player command from console", usage = "<player> <command>", min = 2)
    @CommandPermissions("gtools.playercmd")
    public void playerCmd(CommandContext args, CommandSender sender) {
        Player player = Messaging.getPlayer(args.getString(0), sender);
        if (player != null) {
            Bukkit.dispatchCommand(player, args.getJoinedStrings(1));
        }
    }
}
