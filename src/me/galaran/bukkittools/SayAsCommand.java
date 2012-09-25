package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.Command;
import com.sk89q.minecraft.util.commands.gtools.CommandContext;
import com.sk89q.minecraft.util.commands.gtools.CommandPermissions;
import me.galaran.bukkitutils.gtools.GUtils;
import me.galaran.bukkitutils.gtools.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayAsCommand {

    private final GalaranTools plugin;

    public SayAsCommand(GalaranTools plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = { "sayas" }, desc = "Say as Everyone command", usage = "&1name | &2message", min = 2)
    @CommandPermissions("gtools.sayas")
    public void sayAs(CommandContext args, CommandSender sender) {
        String commandStr = args.getJoinedStrings(0);

        int delimIndex = commandStr.indexOf('|');
        if (delimIndex == -1) {
            delimIndex = commandStr.indexOf(' ');
        }
        String name = commandStr.substring(0, delimIndex).trim();
        String message = commandStr.substring(delimIndex + 1, commandStr.length()).trim();

        String text = ChatColor.GRAY + "[" + name + ChatColor.GRAY + "] " + message;
        text = StringUtils.colorizeAmps(text);
        for (Player curPlayer : Bukkit.getOnlinePlayers()) {
            curPlayer.sendMessage(text);
        }
        GUtils.log(text);
    }
}
