package de.mccityville.lib.database.bukkit.plugin;

import de.mccityville.lib.database.DatabaseExecutorManager;
import de.mccityville.lib.database.bukkit.BukkitDatabaseExecutorManager;
import de.mccityville.lib.database.bukkit.migration.BukkitMigratorFactory;
import de.mccityville.lib.database.bukkit.plugin.command.DbTestCommand;
import de.mccityville.lib.database.bukkit.plugin.command.DbStatsCommand;
import de.mccityville.lib.database.migration.MigratorFactory;
import de.mccityville.libmanager.api.LibraryResolver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Closeable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseContext {

    private DatabaseContext() {
    }

    public static void initialize(LibraryResolver libraryResolver, JavaPlugin plugin) {
        DatabaseExecutorManager executorManager = new BukkitDatabaseExecutorManager(() -> plugin.getConfig().getConfigurationSection("connections"), libraryResolver, plugin);
        MigratorFactory migratorFactory = new BukkitMigratorFactory(executorManager);
        ServicesManager servicesManager = Bukkit.getServicesManager();
        servicesManager.register(DatabaseExecutorManager.class, executorManager, plugin, ServicePriority.Normal);
        servicesManager.register(MigratorFactory.class, migratorFactory, plugin, ServicePriority.Normal);
        plugin.getCommand("dbtest").setExecutor(new DbTestCommand(executorManager, plugin.getLogger()));
        plugin.getCommand("dbstats").setExecutor(new DbStatsCommand(executorManager, plugin.getLogger()));
    }

    public static void shutdown(Logger logger) {
        DatabaseExecutorManager executorManager = Bukkit.getServicesManager().load(DatabaseExecutorManager.class);
        if (executorManager instanceof Closeable) {
            try {
                ((Closeable) executorManager).close();
            } catch (Throwable throwable) {
                logger.log(Level.SEVERE, "An exception occurred while closing executor manager", throwable);
            }
        }
    }
}
