package de.mccityville.lib.database.bukkit.plugin;

import de.mccityville.libmanager.api.DependencyProvisionException;
import de.mccityville.libmanager.api.LibraryManager;
import de.mccityville.libmanager.api.LibraryResolver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.resolution.DependencyResolutionException;

import java.util.logging.Level;

public class DatabasePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("config.example.yml", true);

        LibraryResolver libraryResolver = Bukkit.getServicesManager().load(LibraryManager.class).getLibraryResolver(this);
        try {
            libraryResolver.load("de.mccityville.libdatabase:libdatabase-api:0.1.0-SNAPSHOT");
            libraryResolver.load("de.mccityville.libdatabase:libdatabase-common:0.1.0-SNAPSHOT");
            libraryResolver.load("de.mccityville:libbukkit:0.1.0-SNAPSHOT");
            libraryResolver.load("org.flywaydb:flyway-core:5.0.3");
            libraryResolver.load("com.zaxxer:HikariCP:2.7.4");
        } catch (DependencyResolutionException | DependencyProvisionException e) {
            getLogger().log(Level.SEVERE, "Exception occurred while resolving dependencies", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        DatabaseContext.initialize(libraryResolver, this);
    }

    @Override
    public void onDisable() {
        DatabaseContext.shutdown(getLogger());
    }
}
