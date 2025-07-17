package dev.lorenz.crates.infra.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lorenz.crates.infra.utils.CC;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    @Getter
    private static HikariDataSource dataSource;

    public void start() {
        String host = "localhost";
        int port = 3306;
        String database = "crates";
        String username = "lorenzz";
        String password = "porcodio";

        CC.debug("Connessione MariaDB hardcoded -> host=" + host + ", port=" + port + ", db=" + database + ", user=" + username);
        connect(host, database, username, password, port);
    }

    public void stop() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            CC.database("&cConnessione MariaDB chiusa.");
        } else {
            CC.database("&cNessuna connessione MariaDB da chiudere.");
        }
    }

    private void connect(String host, String database, String user, String password, int port) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true");
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName("org.mariadb.jdbc.Driver");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(10000);
            config.setMaxLifetime(300000);
            config.setConnectionTimeout(10000);
            config.setPoolName("Lorenz-Crates");

            dataSource = new HikariDataSource(config);

            try (Connection connection = dataSource.getConnection()) {
                CC.database("&2&l^o^ &aConnesso al database MariaDB con successo.");
            }

        } catch (Exception e) {
            CC.line();
            CC.error("&4&lU_U &cErrore durante la connessione MariaDB:");
            e.printStackTrace();
            CC.line();
        }
    }


    private void retryConnection(String host, String database, String user, String password, int port) {
        new Thread(() -> {
            while (true) {
                try {
                    CC.database("&eRiprovo a connettermi al database MariaDB tra 5 secondi...");
                    Thread.sleep(5000);
                    connect(host, database, user, password, port);
                    break;
                } catch (Exception ex) {
                    CC.error("&cTentativo di riconnessione fallito: " + ex.getMessage());
                }
            }
        }, "MariaDB-Reconnector").start();
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
