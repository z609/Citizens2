package net.citizensnpcs.command.command;

import java.io.File;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.scripting.CompileCallback;
import net.citizensnpcs.api.scripting.Script;
import net.citizensnpcs.api.scripting.ScriptFactory;
import net.citizensnpcs.command.Command;
import net.citizensnpcs.command.CommandContext;
import net.citizensnpcs.command.ServerCommand;
import net.citizensnpcs.command.exception.CommandException;
import net.citizensnpcs.util.Messaging;

import org.bukkit.command.CommandSender;

import com.google.common.base.Splitter;

public class ScriptCommands {
    private final Citizens plugin;

    public ScriptCommands(Citizens plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = { "script" },
            modifiers = { "compile", "run" },
            usage = "compile|run [file]",
            desc = "compile and run a script",
            min = 2,
            max = 2,
            permission = "script.compile")
    @ServerCommand
    public void runScript(final CommandContext args, final CommandSender sender, NPC npc) throws CommandException {
        File file = new File(plugin.getDataFolder(), args.getString(1));
        if (!file.exists())
            throw new CommandException("The file '" + args.getString(1) + "' doesn't exist!");
        boolean success = CitizensAPI.getScriptCompiler().compile(file).withCallback(new CompileCallback() {
            @Override
            public void onScriptCompiled(ScriptFactory script) {
                Script s = script.newInstance();
                if (args.hasValueFlag("i")) {
                    for (String m : Splitter.on(',').split(args.getFlag("i"))) {
                        s.invoke(m, new Object[] {});
                    }
                }
                Messaging.send(sender, "<a>Done.");
            }
        }).begin();
        if (success) {
            sender.sendMessage("Compiling...");
        } else {
            sender.sendMessage("Could not schedule compilation.");
        }
    }
}