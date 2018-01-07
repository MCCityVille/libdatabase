package de.mccityville.lib.database.bukkit.plugin.command;

import com.zaxxer.hikari.HikariDataSource;
import de.mccityville.lib.database.DatabaseExecutor;
import de.mccityville.lib.database.DatabaseExecutorManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.sql.DataSource;
import java.util.logging.Logger;

public class DbTestCommand extends AbstractDbCommand {

    public DbTestCommand(DatabaseExecutorManager databaseExecutorManager, Logger logger) {
        super(databaseExecutorManager, logger);
    }

    @Override
    protected boolean onCommand0(CommandSender sender, Command command, String label, String contextName, DatabaseExecutor context, String[] args) {
        String testSql = getTestSql(context);
        if (testSql == null) {
            sender.sendMessage(ChatColor.RED + "Cannot get test query for " + contextName);
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Executing connection test for " + contextName + "...");
        context.select(testSql).subscribe(
                rs -> sender.sendMessage(ChatColor.GREEN + "Connection successfully tested for " + contextName),
                throwable -> sender.sendMessage(ChatColor.RED + "Connection error occurred for " + contextName + ": " + throwable.getMessage())
        );
        return true;
    }

    private static String getTestSql(DatabaseExecutor executor) {
        DataSource dataSource = executor.getDataSource();
        if (dataSource instanceof HikariDataSource)
            return ((HikariDataSource) dataSource).getConnectionTestQuery();
        return null;
    }
}
