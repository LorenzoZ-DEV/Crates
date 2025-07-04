package dev.lorenz.crates.application.crates;

import dev.lorenz.crates.application.manager.Manager;
import dev.lorenz.crates.infra.CC;
import dev.lorenz.crates.infra.sql.DatabaseManager;

import java.sql.*;
import java.util.UUID;

public class VirtualKeyManager implements Manager
{

    @Override
    public void start() {
        CC.info("Starting virtual key manager...");
        createTable();
    }
    @Override
    public void stop() {
        CC.info("Stopping virtual key manager...");
    }
    private void createTable() {
        try (
                Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS crate_virtual_keys (
                    uuid VARCHAR(36) NOT NULL,
                    crate_id VARCHAR(64) NOT NULL,
                    amount INT NOT NULL DEFAULT 0,
                    PRIMARY KEY (uuid, crate_id)
                );
            """);
            CC.debug ("&aTabella crate_virtual_keys creata/verificata.");
        } catch (SQLException e) {
            CC.error("&cErrore nella creazione/verifica della tabella crate_virtual_keys:");
            e.printStackTrace();
        }
    }

    public int getKeys(UUID uuid, String crateId) {
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT amount FROM crate_virtual_keys WHERE uuid = ? AND crate_id = ?")
        ) {
            ps.setString(1, uuid.toString());
            ps.setString(2, crateId.toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("amount");
                }
            }

        } catch (SQLException e) {
            CC.error("&cErrore nel recupero chiavi virtuali:");
            e.printStackTrace();
        }

        return 0;
    }

    public void setKeys(UUID uuid, String crateId, int amount) {
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("REPLACE INTO crate_virtual_keys (uuid, crate_id, amount) VALUES (?, ?, ?)")
        ) {
            ps.setString(1, uuid.toString());
            ps.setString(2, crateId.toLowerCase());
            ps.setInt(3, amount);
            ps.executeUpdate();

        } catch (SQLException e) {
            CC.line ();
            CC.error("&cErrore nel salvataggio crates virtuali:");
            e.printStackTrace();
            CC.line ();
        }
    }

    public void addKeys(UUID uuid, String crateId, int toAdd) {
        int current = getKeys(uuid, crateId);
        setKeys(uuid, crateId, current + toAdd);
    }

    public void removeKeys(UUID uuid, String crateId, int toRemove) {
        int current = getKeys(uuid, crateId);
        setKeys(uuid, crateId, Math.max(0, current - toRemove));
    }

    public boolean hasKeys(UUID uuid, String crateId, int required) {
        return getKeys(uuid, crateId) >= required;
    }
}
