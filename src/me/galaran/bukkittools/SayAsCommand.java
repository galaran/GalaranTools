package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.Command;
import com.sk89q.minecraft.util.commands.gtools.CommandContext;
import com.sk89q.minecraft.util.commands.gtools.CommandPermissions;
import me.galaran.bukkitutils.gtools.text.Messaging;
import me.galaran.bukkitutils.gtools.text.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayAsCommand {

    public SayAsCommand(GalaranTools plugin) { }

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

        String result = StringUtils.colorizeAmps("&7[" + name + "&7] " + message);
        for (Player curPlayer : Bukkit.getOnlinePlayers()) {
            curPlayer.sendMessage(result);
        }
        Messaging.log(result);
    }
}
