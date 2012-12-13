package me.galaran.bukkittools;

import com.sk89q.minecraft.util.commands.gtools.Command;
import com.sk89q.minecraft.util.commands.gtools.CommandContext;
import com.sk89q.minecraft.util.commands.gtools.NestedCommand;
import org.bukkit.command.CommandSender;

public class GTCommands {

    public GTCommands(GalaranTools plugin) { }

    @Command(aliases = { "gtools", "gt" }, desc = "GTools commands")
    @NestedCommand({ GeneralCommands.class })
    public void gToolsCommand(CommandContext args, CommandSender sender) { }
}
