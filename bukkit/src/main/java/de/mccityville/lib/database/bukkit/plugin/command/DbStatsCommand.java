package de.mccityville.lib.database.bukkit.plugin.command;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import de.mccityville.lib.database.DatabaseExecutor;
import de.mccityville.lib.database.DatabaseExecutorManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.sql.DataSource;
import java.util.logging.Logger;

public class DbStatsCommand extends AbstractDbCommand {

    public DbStatsCommand(DatabaseExecutorManager databaseExecutorManager, Logger logger) {
        super(databaseExecutorManager, logger);
    }

    @Override
    protected boolean onCommand0(CommandSender sender, Command command, String label, String contextName, DatabaseExecutor context, String[] args) {
        printStats(contextName, context, sender);
        return true;
    }

    private static void printStats(String executorName, DatabaseExecutor executor, CommandSender sender) {
        DataSource dataSource = executor.getDataSource();
        if (dataSource instanceof HikariDataSource) {
            HikariPoolMXBean stats = ((HikariDataSource) dataSource).getHikariPoolMXBean();
            sender.sendMessage(ChatColor.GREEN + "Database pool statistics for " + executorName + ':');
            sender.sendMessage(ChatColor.GREEN + "  Active connections: " + ChatColor.GRAY + stats.getActiveConnections());
            sender.sendMessage(ChatColor.GREEN + "  Idle connections: " + ChatColor.GRAY + stats.getIdleConnections());
            sender.sendMessage(ChatColor.GREEN + "  Total connections: " + ChatColor.GRAY + stats.getTotalConnections());
            sender.sendMessage(ChatColor.GREEN + "  Blocked threads: " + ChatColor.GRAY + stats.getThreadsAwaitingConnection());
        } else {
            sender.sendMessage(ChatColor.RED + "Cannot get database pool statistics");
        }
    }
}
