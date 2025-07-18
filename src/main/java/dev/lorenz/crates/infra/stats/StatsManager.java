package dev.lorenz.crates.infra.stats;

import dev.lorenz.crates.application.stats.LeaderboardEntry;
import dev.lorenz.crates.infra.utils.CC;
import dev.lorenz.crates.infra.sql.DatabaseManager;
import dev.lorenz.crates.application.manager.Manager;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class StatsManager implements Manager {

    @Override
    public void start() {
        createTable();
        CC.info("StatsManager avviato.");
    }

    @Override
    public void stop() {
        CC.info("StatsManager arrestato.");
    }

    private void createTable() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS crate_stats (" +
                             "uuid VARCHAR(36) NOT NULL," +
                             "crate_id VARCHAR(64) NOT NULL," +
                             "opened_crates INT NOT NULL DEFAULT 0," +
                             "keys_used INT NOT NULL DEFAULT 0," +
                             "PRIMARY KEY (uuid, crate_id)" +
                             ");"
             )) {
            ps.executeUpdate();
            CC.debug("&aTabella crate_stats creata/verificata.");
        } catch (SQLException e) {
            CC.line();
            CC.error("&cErrore nella creazione/verifica della tabella crate_stats:");
            e.printStackTrace();
            CC.line();
        }
    }

    public int getOpenedCrates(UUID uuid) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT SUM(opened_crates) as total FROM crate_stats WHERE uuid = ?"
             )) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recupero totale opened_crates:");
            e.printStackTrace();
        }
        return 0;
    }

    public int getKeysUsed(UUID uuid) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT SUM(keys_used) as total FROM crate_stats WHERE uuid = ?"
             )) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recupero totale keys_used:");
            e.printStackTrace();
        }
        return 0;
    }

    public List<LeaderboardEntry> getLeaderboard(int page, int limit) {
        List<LeaderboardEntry> list = new ArrayList<>();
        int offset = (page - 1) * limit;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT uuid, SUM(opened_crates) AS total FROM crate_stats GROUP BY uuid ORDER BY total DESC LIMIT ? OFFSET ?"
             )) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int totalOpened = rs.getInt("total");
                    list.add(new LeaderboardEntry(uuid, totalOpened));
                }
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recuperare la leaderboard globale: " + e.getMessage());
        }

        return list;
    }

    public int getOpenedCrates(UUID uuid, String crateId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT opened_crates FROM crate_stats WHERE uuid = ? AND crate_id = ?"
             )) {
            ps.setString(1, uuid.toString());
            ps.setString(2, crateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("opened_crates");
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recupero opened_crates per crate " + crateId + ":");
            e.printStackTrace();
        }
        return 0;
    }

    public int getKeysUsed(UUID uuid, String crateId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT keys_used FROM crate_stats WHERE uuid = ? AND crate_id = ?"
             )) {
            ps.setString(1, uuid.toString());
            ps.setString(2, crateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("keys_used");
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recupero keys_used per crate " + crateId + ":");
            e.printStackTrace();
        }
        return 0;
    }

    public List<LeaderboardEntry> getLeaderboard(String crateId, int page, int limit) {
        List<LeaderboardEntry> list = new ArrayList<>();
        int offset = (page - 1) * limit;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT uuid, opened_crates FROM crate_stats WHERE crate_id = ? ORDER BY opened_crates DESC LIMIT ? OFFSET ?"
             )) {
            ps.setString(1, crateId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int opened = rs.getInt("opened_crates");
                    list.add(new LeaderboardEntry(uuid, opened));
                }
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recuperare la leaderboard per crate '" + crateId + "': " + e.getMessage());
        }

        return list;
    }


    public void addOpenedCrates(UUID uuid, String crateId, int amount) {
        int current = getOpenedCrates(uuid, crateId);
        int keys = getKeysUsed(uuid, crateId);
        setStats(uuid, crateId, current + amount, keys);
    }

    public void addKeysUsed(UUID uuid, String crateId, int amount) {
        int current = getKeysUsed(uuid, crateId);
        int opened = getOpenedCrates(uuid, crateId);
        setStats(uuid, crateId, opened, current + amount);
    }

    private void setStats(UUID uuid, String crateId, int openedCrates, int keysUsed) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO crate_stats (uuid, crate_id, opened_crates, keys_used) VALUES (?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE opened_crates = ?, keys_used = ?"
             )) {
            ps.setString(1, uuid.toString());
            ps.setString(2, crateId);
            ps.setInt(3, openedCrates);
            ps.setInt(4, keysUsed);
            ps.setInt(5, openedCrates);
            ps.setInt(6, keysUsed);
            ps.executeUpdate();
        } catch (SQLException e) {
            CC.line();
            CC.error("&cErrore nel salvataggio delle statistiche per crate " + crateId + ":");
            e.printStackTrace();
            CC.line();
        }
    }
}
