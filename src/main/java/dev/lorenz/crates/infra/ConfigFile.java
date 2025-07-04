package dev.lorenz.crates.infra;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class ConfigFile extends YamlConfiguration {
    private final Plugin plugin;
    private final String fileName;
    private final Path filePath;

    public ConfigFile(Plugin plugin, String fileName, boolean load, boolean forceCreate) {
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        this.filePath = Path.of(plugin.getDataFolder().getPath(), this.fileName);
        this.ensureDataFolder();

        try {
            if (forceCreate) {
                Files.deleteIfExists(this.filePath);
            }

            if (Files.notExists(this.filePath, new LinkOption[0])) {
                plugin.saveResource(this.fileName, false);
            }

            if (load) {
                super.load(this.filePath.toFile());
                InputStream resource = plugin.getResource(this.fileName);
                if (resource != null) {
                    try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                        YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
                        this.setDefaults(defaults);
                        this.options().copyDefaults(true);
                    }
                }

                super.save(this.filePath.toFile());
            }
        } catch (InvalidConfigurationException | IOException ex) {
            CC.line ();
            CC.error ("Error initializing config file: " + fileName);
            ((Exception)ex).printStackTrace();
            CC.line ();
        }

    }

    public ConfigFile(Plugin plugin, String fileName, boolean load) {
        this(plugin, fileName, load, false);
    }

    public ConfigFile(Plugin plugin, String fileName) {
        this(plugin, fileName, true, false);
    }

    private void ensureDataFolder() {
        try {
            Files.createDirectories(Path.of(this.plugin.getDataFolder().getPath()));
        } catch (IOException e) {
            CC.line ();
            CC.error("Could not create plugin data folder");
            e.printStackTrace();
            CC.line ();

        }

    }

    public void reload() {
        try {
            super.load(this.filePath.toFile());
            CC.line ();
            CC.info ("Reloaded config file: " + this.fileName);
            CC.line ();
        } catch (InvalidConfigurationException | IOException ex) {
            CC.line ();
            CC.error ("Error reloading config file: " + this.fileName);
            ((Exception)ex).printStackTrace();
            CC.line ();
        }

    }

    public void saveFile() {
        try {
            super.save(this.filePath.toFile());
            CC.info ("Saved config file: " + this.fileName);
        } catch (IOException ex) {
            CC.line ();
            CC.error ("Error saving config file: " + this.fileName);
            ex.printStackTrace();
            CC.line ();
        }

    }

    public void setDefault(String path, Object value) {
        if (!this.contains(path)) {
            this.set(path, value);
        }

    }

    public void deleteFile() {
        try {
            Files.deleteIfExists(this.filePath);
            this.plugin.getLogger().info("Deleted config file: " + this.fileName);
        } catch (IOException ex) {
            this.plugin.getLogger().severe("Error deleting config file: " + this.fileName);
            ex.printStackTrace();
        }

    }
}

