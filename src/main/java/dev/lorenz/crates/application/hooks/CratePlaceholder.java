package dev.lorenz.crates.application.hooks;

import dev.lorenz.crates.CratePlugin;
import dev.lorenz.crates.application.stats.LeaderboardEntry;
import dev.lorenz.crates.application.stats.StatsManager;
import dev.lorenz.crates.infra.CC;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CratePlaceholder extends PlaceholderExpansion {

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
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("leaderboard_")) {
            String[] parts = identifier.split("_");
            if (parts.length != 4) return CC.translate ( "&cValori non validi");

            String crateId = parts[1];
            int page, row;
            try {
                page = Integer.parseInt(parts[2]);
                row = Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                return CC.translate ( "&cValori non validi");
            }

            StatsManager stats = CratePlugin.getINSTANCE ().getService().get(StatsManager.class);
            if (stats == null) return CC.translate ( "&cErrore nel recupero delle statistiche");

            List<LeaderboardEntry> leaderboard = stats.getLeaderboard(page, 10);
            if (row < 1 || row > leaderboard.size()) return CC.translate ( "&7N/A");

            LeaderboardEntry entry = leaderboard.get(row - 1);
            String name = Bukkit.getOfflinePlayer(entry.uuid()).getName();
            return "§e#" + ((page - 1) * 10 + row) + " §f" + name + " §7(" + entry.opened() + ")";
        }

        return null;
    }
}
