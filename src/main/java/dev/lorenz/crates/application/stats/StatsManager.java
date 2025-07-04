package dev.lorenz.crates.application.stats;

import dev.lorenz.crates.CratePlugin;
import dev.lorenz.crates.infra.CC;
import dev.lorenz.crates.infra.sql.DatabaseManager;
import dev.lorenz.crates.application.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class StatsManager implements Manager {

    public void start() {
        createTable();
        CC.info("StatsManager avviato.");
    }

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



    public int getOpenedCrates(UUID uuid, String crateId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT opened_crates FROM crate_stats WHERE uuid = ? AND crate_id = ?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, crateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("opened_crates");
                }
            }
        } catch (SQLException e) {
            CC.line();
            CC.error("&cErrore nel recupero opened_crates per crate " + crateId + ":");
            e.printStackTrace();
            CC.line();
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
            CC.error("Errore nel recuperare leaderboard per cassa '" + crateId + "': " + e.getMessage());
        }

        return list;
    }




    public int getKeysUsed(UUID uuid, String crateId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT keys_used FROM crate_stats WHERE uuid = ? AND crate_id = ?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, crateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("keys_used");
                }
            }
        } catch (SQLException e) {
            CC.line();
            CC.error("&cErrore nel recupero keys_used per crate " + crateId + ":");
            e.printStackTrace();
            CC.line();
        }
        return 0;
    }


    public void addOpenedCrates(UUID uuid, String crateId, int amount) {
        int current = getOpenedCrates(uuid, crateId);
        setStats(uuid, crateId, current + amount, getKeysUsed(uuid, crateId));
    }


    public void addKeysUsed(UUID uuid, String crateId, int amount) {
        int current = getKeysUsed(uuid, crateId);
        setStats(uuid, crateId, getOpenedCrates(uuid, crateId), current + amount);
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