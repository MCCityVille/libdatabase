package de.mccityville.lib.database.migration;

import de.mccityville.lib.database.DatabaseExecutorManager;

public interface MigratorFactory {

    default Migrator createDefault(Object domain) {
        return create(DatabaseExecutorManager.DEFAULT_DATABASE_EXECUTOR_NAME, domain);
    }

    Migrator create(String name, Object domain);
}
