package de.mccityville.lib.database.bukkit.migration;

import de.mccityville.lib.database.migration.Migrator;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.callback.BaseFlywayCallback;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BukkitMigrator implements Migrator {

    private final Plugin domain;
    private final DataSource dataSource;

    @Override
    public void executeMigrations() throws Exception {
        domain.getLogger().info("Starting database migrations...");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", domain.getName());
        Flyway flyway = new Flyway(domain.getClass().getClassLoader());
        flyway.setDataSource(dataSource);
        flyway.setPlaceholders(placeholders);
        flyway.setTable(domain.getName() + "_migrations");
        flyway.setInstalledBy("server:" + domain.getServer().getServerName());
        flyway.setCallbacks(new DefaultCallback());
        int migrations = flyway.migrate();
        if (migrations > 0)
            domain.getLogger().info("Successfully applied " + migrations + " migrations");
        else
            domain.getLogger().info("No migrations were applied");
    }

    private class DefaultCallback extends BaseFlywayCallback {

        @Override
        public void beforeEachMigrate(Connection connection, MigrationInfo info) {
            domain.getLogger().info("Migrate " + info.getVersion() + " from script " + info.getScript() + " (checksum: " + info.getChecksum() + ")...");
        }

        @Override
        public void beforeEachUndo(Connection connection, MigrationInfo info) {
            domain.getLogger().info("Undo " + info.getVersion() + " from script " + info.getScript() + " (checksum: " + info.getChecksum() + ")...");
        }
    }
}
