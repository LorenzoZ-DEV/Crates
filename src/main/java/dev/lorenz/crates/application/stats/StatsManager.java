package dev.lorenz.crates.application.stats;

import dev.lorenz.crates.infra.CC;
import dev.lorenz.crates.infra.sql.DatabaseManager;
import dev.lorenz.crates.application.manager.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
                             "uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                             "opened_crates INT NOT NULL DEFAULT 0," +
                             "keys_used INT NOT NULL DEFAULT 0" +
                             ");"
             )) {
            ps.executeUpdate();
            CC.debug ("&aTabella crate_stats creata/verificata.");
        } catch (SQLException e) {
            CC.error("&cErrore nella creazione/verifica della tabella crate_stats:");
            e.printStackTrace();
        }
    }

    public int getOpenedCrates(UUID uuid) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT opened_crates FROM crate_stats WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("opened_crates");
                }
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recupero opened_crates:");
            e.printStackTrace();
        }
        return 0;
    }

    public int getKeysUsed(UUID uuid) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT keys_used FROM crate_stats WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("keys_used");
                }
            }
        } catch (SQLException e) {
            CC.error("&cErrore nel recupero keys_used:");
            e.printStackTrace();
        }
        return 0;
    }

    public void addOpenedCrates(UUID uuid, int amount) {
        int current = getOpenedCrates(uuid);
        setStats(uuid, current + amount, getKeysUsed(uuid));
    }

    public void addKeysUsed(UUID uuid, int amount) {
        int current = getKeysUsed(uuid);
        setStats(uuid, getOpenedCrates(uuid), current + amount);
    }

    private void setStats(UUID uuid, int openedCrates, int keysUsed) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO crate_stats (uuid, opened_crates, keys_used) VALUES (?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE opened_crates = ?, keys_used = ?"
             )) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, openedCrates);
            ps.setInt(3, keysUsed);
            ps.setInt(4, openedCrates);
            ps.setInt(5, keysUsed);
            ps.executeUpdate();
        } catch (SQLException e) {
            CC.error("&cErrore nel salvataggio delle statistiche:");
            e.printStackTrace();
        }
    }
}
