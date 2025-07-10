package dev.lorenz.crates.infra.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lorenz.crates.bootstrap.CratePlugin;
import dev.lorenz.crates.infra.utils.CC;
import dev.lorenz.crates.infra.utils.ConfigFile;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    @Getter
    private static HikariDataSource dataSource;

    public void start() {
        ConfigFile file = CratePlugin.getINSTANCE().getStorageFile();

        String host = file.getString("MYSQL.host");
        int port = file.getInt("MYSQL.port");
        String database = file.getString("MYSQL.database");
        String username = file.getString("MYSQL.username");
        String password = file.getString("MYSQL.password");

        // Log di debug per verificare se li sta leggendo bene
        CC.debug("MySQL Config -> host=" + host + ", port=" + port + ", db=" + database + ", user=" + username + ", pass=" + password);

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
            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
            CC.debug("Tentativo di connessione con: " + jdbcUrl);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(10000);
            config.setMaxLifetime(300000);
            config.setConnectionTimeout(10000);
            config.setPoolName("Lorenz-Crates");

            dataSource = new HikariDataSource(config);

            try (Connection connection = dataSource.getConnection()) {
                CC.database("&2&l^o^ &aConnesso al database MySQL con successo.");
            }

        } catch (Exception e) {
            CC.line();
            CC.error("&4&lU_U &cErrore durante la connessione MySQL:");
            e.printStackTrace();
            CC.line();
        }
    }



    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
