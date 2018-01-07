package de.mccityville.lib.database.bukkit.plugin.command;

import de.mccityville.lib.database.DatabaseExecutor;
import de.mccityville.lib.database.DatabaseExecutorManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public abstract class AbstractDbCommand implements CommandExecutor {

    private final DatabaseExecutorManager databaseExecutorManager;
    private final Logger logger;

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String contextName = args.length > 0 ? args[0] : DatabaseExecutorManager.DEFAULT_DATABASE_EXECUTOR_NAME;
        DatabaseExecutor context;
        try {
            context = databaseExecutorManager.getExecutor(contextName);
        } catch (Throwable throwable) {
            sender.sendMessage(ChatColor.RED + "An error occurred: " + throwable.getMessage());
            logger.log(Level.SEVERE, "An exception occurred while getting database context", throwable);
            return true;
        }
        String[] remainingArgs = new String[Math.max(args.length - 1, 0)];
        if (remainingArgs.length > 0)
            System.arraycopy(args, 1, remainingArgs, 0, remainingArgs.length);
        return onCommand0(sender, command, label, contextName, context, remainingArgs);
    }

    protected abstract boolean onCommand0(CommandSender sender, Command command, String label, String contextName, DatabaseExecutor context, String[] args);
}
