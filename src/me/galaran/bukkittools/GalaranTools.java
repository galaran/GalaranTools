package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.*;
import me.galaran.bukkitutils.gtools.GUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GalaranTools extends JavaPlugin {

    private CommandsManager<CommandSender> commands;

    @Override
    public void onEnable() {
        GUtils.init(this.getLogger(), "GTools");

        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender.hasPermission(perm);
            }
        };
        commands.setInjector(new SimpleInjector(this));

        commands.register(GTCommands.class);
        commands.register(SayAsCommand.class);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            commands.execute(command.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            GUtils.sendMessage(sender, ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            GUtils.sendMessage(sender, ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            GUtils.sendMessage(sender, ChatColor.RED + e.getMessage());
            GUtils.sendMessage(sender, ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                GUtils.sendMessage(sender, ChatColor.RED + "Number expected, string received instead.");
            } else {
                GUtils.sendMessage(sender, ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            GUtils.sendMessage(sender, ChatColor.RED + e.getMessage());
        }

        return true;
    }
}
