package dev.lorenz.crates.infra.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lorenz.crates.infra.CC;
import dev.lorenz.crates.infra.ConfigFile;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private static HikariDataSource dataSource;

    public void start(ConfigFile storageFile) {
        String host = storageFile.getString("MYSQL.host");
        int port = storageFile.getInt("MYSQL.port");
        String database = storageFile.getString("MYSQL.database");
        String username = storageFile.getString("MYSQL.username");
        String password = storageFile.getString("MYSQL.password");

        connect(host, database, username, password, port);
    }

    public void stop() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            CC.database("&cConnessione MySQL chiusa.");
        } else {
            CC.database("&cNessuna connessione MySQL da chiudere.");
        }
    }

    private void connect(String host, String database, String user, String password, int port) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true");
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(10000);
            config.setMaxLifetime(300000);
            config.setConnectionTimeout(10000);
            config.setPoolName("CratesMySQL");

            dataSource = new HikariDataSource(config);

            try (Connection connection = dataSource.getConnection()) {
                CC.database("&2&l^o^ &aConnesso al database MySQL con successo.");
            }

        } catch (Exception e) {
            CC.line();
            CC.error("&4&lU_U &cErrore durante la connessione MySQL:");
            e.printStackTrace();
            CC.line ();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
