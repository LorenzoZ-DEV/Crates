package dev.lorenz.crates.application;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CratePlaceholder extends PlaceholderExpansion
{
    private final JavaPlugin plugin;

    public CratePlaceholder(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "crates";
    }
    @Override
    public String getAuthor() {
        return "Lorenz";
    }
    @Override
    public String getVersion() {
        return plugin.getDescription ().getVersion();
    }
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("leaderboard_")) {
            String[] parts = identifier.split("_");
            if (parts.length != 4) return "Invalid format";

            String crate = parts[1];
            int page = Integer.parseInt(parts[2]);
            int row = Integer.parseInt(parts[3]);

            // TODO: Implementazione to SQL
            return "Player#" + row;
        }

        return null;
    }
}
