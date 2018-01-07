package de.mccityville.lib.database.bukkit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.mccityville.bukkit.reactive.BukkitReactiveScheduler;
import de.mccityville.lib.database.DatabaseExecutor;
import de.mccityville.lib.database.DatabaseExecutorManager;
import de.mccityville.lib.database.ScheduledDatabaseExecutor;
import de.mccityville.libmanager.api.DependencyProvisionException;
import de.mccityville.libmanager.api.LibraryResolver;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.eclipse.aether.resolution.DependencyResolutionException;

import javax.sql.DataSource;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

@RequiredArgsConstructor
public class BukkitDatabaseExecutorManager implements DatabaseExecutorManager, Closeable {

    private final Map<String, DatabaseExecutor> databaseExecutors = new HashMap<>();
    private final Supplier<ConfigurationSection> baseConfigurationSupplier;
    private final LibraryResolver libraryResolver;
    private final Plugin plugin;

    @Override
    public void close() {
        Iterator<Map.Entry<String, DatabaseExecutor>> iterator = databaseExecutors.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DatabaseExecutor> entry = iterator.next();
            DataSource dataSource = entry.getValue().getDataSource();
            if (dataSource instanceof Closeable) {
                plugin.getLogger().info("Closing " + entry.getKey() + "...");
                try {
                    ((Closeable) dataSource).close();
                } catch (Throwable throwable) {
                    plugin.getLogger().log(Level.SEVERE, "An exception occurred while closing datasource", throwable);
                }
            }
            iterator.remove();
        }
    }

    @Override
    public DatabaseExecutor getExecutor(String name) {
        return databaseExecutors.computeIfAbsent(name, n -> {
            ConfigurationSection baseConfiguration = baseConfigurationSupplier.get();
            ConfigurationSection config = baseConfiguration != null ? baseConfiguration.getConfigurationSection(n) : null;
            if (config == null)
                throw new IllegalArgumentException("Unknown database connection: " + n);
            DataSource dataSource = extract(config);
            if (dataSource == null)
                throw new UnsupportedOperationException("Initialization failed");
            return new ScheduledDatabaseExecutor(dataSource, BukkitReactiveScheduler.createAsynchronous(plugin));
        });
    }

    private HikariDataSource extract(ConfigurationSection source) {
        HikariConfig config = new HikariConfig();
        List<String> requiredLibraries = source.getStringList("required_libraries");
        if (requiredLibraries != null && !requiredLibraries.isEmpty()) {
            plugin.getLogger().info("Database configuration requested libraries: " + requiredLibraries);
            for (String requiredLibrary : requiredLibraries) {
                try {
                    libraryResolver.load(requiredLibrary);
                } catch (DependencyResolutionException | DependencyProvisionException e) {
                    plugin.getLogger().log(Level.SEVERE, "Exception occurred while resolving required library for database driver", e);
                    return null;
                }
            }
        }
        config.setDriverClassName(source.getString("driver_class_name", config.getDriverClassName()));
        config.setJdbcUrl(source.getString("url", config.getJdbcUrl()));
        config.setUsername(source.getString("username", config.getUsername()));
        config.setPassword(source.getString("password", config.getPassword()));
        if (source.contains("auto_commit"))
            config.setAutoCommit(source.getBoolean("auto_commit"));
        if (source.contains("init_sql"))
            config.setConnectionInitSql(source.getString("init_sql"));
        if (source.contains("connection_test_query"))
            config.setConnectionTestQuery(source.getString("connection_test_query"));
        if (source.contains("connection_timeout"))
            config.setConnectionTimeout(source.getLong("connection_timeout"));
        if (source.contains("max_pool_size"))
            config.setMaximumPoolSize(source.getInt("max_pool_size"));
        ConfigurationSection properties = source.getConfigurationSection("properties");
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.getValues(false).entrySet())
                config.addDataSourceProperty(entry.getKey(), entry.getValue());
        }
        config.validate();
        return new HikariDataSource(config);
    }
}
