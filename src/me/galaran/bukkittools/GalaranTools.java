package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.*;
import me.galaran.bukkitutils.gtools.text.Messaging;
import me.galaran.bukkitutils.gtools.text.TranslationBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GalaranTools extends JavaPlugin {

    private CommandsManager<CommandSender> commands;

    @Override
    public void onEnable() {
        Messaging.init(getLogger(), ChatColor.GRAY + "[GTools] ", new TranslationBase("/strings.properties"));

        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender.hasPermission(perm);
            }
        };
        commands.setInjector(new SimpleInjector(this));

        commands.register(GTCommands.class);
        commands.register(SayAsCommand.class);
        commands.register(PlayerCmdCommand.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            commands.execute(command.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            Messaging.send(sender, "utils.no-perm");
        } catch (MissingNestedCommandException e) {
            Messaging.sendRaw(sender, ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            Messaging.sendRaw(sender, ChatColor.RED + e.getMessage());
            Messaging.sendRaw(sender, ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                Messaging.send(sender, "command.not-a-number");
            } else {
                Messaging.send(sender, "command.error-console");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            Messaging.sendRaw(sender, ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
