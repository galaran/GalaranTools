package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.*;
import me.galaran.bukkitutils.gtools.GUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

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
            GUtils.sendMessage(sender, "You don't have permission.", ChatColor.RED);
        } catch (MissingNestedCommandException e) {
            GUtils.sendMessage(sender, e.getUsage(), ChatColor.RED);
        } catch (CommandUsageException e) {
            GUtils.sendMessage(sender, e.getMessage(), ChatColor.RED);
            GUtils.sendMessage(sender, e.getUsage(), ChatColor.RED);
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                GUtils.sendMessage(sender, "Number expected, string received instead.", ChatColor.RED);
            } else {
                GUtils.sendMessage(sender, "An error has occurred. See console.", ChatColor.RED);
                e.printStackTrace();
            }
        } catch (CommandException e) {
            GUtils.sendMessage(sender, e.getMessage(), ChatColor.RED);
        }

        return true;
    }
}
