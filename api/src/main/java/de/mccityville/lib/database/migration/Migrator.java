package de.mccityville.lib.database.migration;

public interface Migrator {

    void executeMigrations() throws Exception;
}
