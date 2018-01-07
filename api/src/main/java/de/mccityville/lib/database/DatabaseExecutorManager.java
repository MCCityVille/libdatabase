package de.mccityville.lib.database;

public interface DatabaseExecutorManager {

    String DEFAULT_DATABASE_EXECUTOR_NAME = "_default";

    DatabaseExecutor getExecutor(String name);

    default DatabaseExecutor getDefaultExecutor() {
        return getExecutor(DEFAULT_DATABASE_EXECUTOR_NAME);
    }
}
