package de.mccityville.lib.database.bukkit.migration;

import de.mccityville.lib.database.DatabaseExecutor;
import de.mccityville.lib.database.DatabaseExecutorManager;
import de.mccityville.lib.database.migration.Migrator;
import de.mccityville.lib.database.migration.MigratorFactory;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class BukkitMigratorFactory implements MigratorFactory {

    private final DatabaseExecutorManager databaseExecutorManager;

    @Override
    public Migrator create(String name, Object domain) {
        if (!(domain instanceof Plugin))
            throw new IllegalArgumentException("Only Plugins are supported as a domain object");
        DatabaseExecutor databaseExecutor = databaseExecutorManager.getExecutor(name);
        DataSource dataSource = databaseExecutor.getDataSource();
        return new BukkitMigrator((Plugin) domain, dataSource);
    }
}
