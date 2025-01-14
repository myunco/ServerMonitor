package net.myunco.servermonitor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL implements DataSource {
    private Connection connection;

    public MySQL() {
        try {
            getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String host = "localhost";
            int port = 3306;
            String username = "root";
            String password = "";
            String database = "mc";
            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            if (connection == null) {
                throw new SQLException("连接数据库失败");
            } else {
                System.out.println("连接数据库成功");
                connection.setAutoCommit(false);
                String[] createTableSqls = {
                    "CREATE TABLE IF NOT EXISTS `chat_log` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(4096) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                    "CREATE TABLE IF NOT EXISTS `command_log` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(4096) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `command` varchar(4096) NOT NULL," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  `op` char(3) NOT NULL DEFAULT 'NO'," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                    "CREATE TABLE IF NOT EXISTS `gamemode_log` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `gamemode` varchar(10) NOT NULL," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                    "CREATE TABLE IF NOT EXISTS `join_leave_log` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `ip` varchar(22) NOT NULL," +
                    "  `kick_reason` varchar(256)," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                    "CREATE TABLE IF NOT EXISTS `op_change_log` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `command_sender` varchar(255) NOT NULL COMMENT '操作源'," +
                    "  `target_player` char(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  `type` tinyint(1) NOT NULL COMMENT '操作类型 0=撤销OP权限 1=授予OP权限'," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                    "CREATE TABLE IF NOT EXISTS `warning_log` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `player` varchar(255) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                    "CREATE TABLE IF NOT EXISTS `keywords_alert_log` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `command` varchar(1024) NOT NULL," +
                    "  `player` varchar(255) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;"
                };

                for (String sql : createTableSqls) {
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.executeUpdate();
                    }
                }
                connection.commit();
                connection.setAutoCommit(true);
            }
        }
        return connection;
    }

    @Override
    public void logChat(String text, String player, String uuid){
        String sql = "INSERT INTO chat_log (text, player, uuid) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, text);
            stmt.setString(2, player);
            stmt.setString(3, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logCommand(String text, String command, String player, String uuid, boolean isOp) {
        String sql = "INSERT INTO command_log (text, command, player, uuid, op) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, text);
            stmt.setString(2, command);
            stmt.setString(3, player);
            stmt.setString(4, uuid);
            stmt.setString(5, isOp ? "YES" : "NO");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logGameModeChange(String text, String gamemode, String player, String uuid) {
        String sql = "INSERT INTO gamemode_log (text, gamemode, player, uuid) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, text);
            stmt.setString(2, gamemode);
            stmt.setString(3, player);
            stmt.setString(4, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logJoinLeave(String text, String player, String ip, String kickReason) {
        String sql = "INSERT INTO join_leave_log (text, player, ip, kick_reason) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, text);
            stmt.setString(2, player);
            stmt.setString(3, ip);
            stmt.setString(4, kickReason);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logOpChange(String text, String commandSender, String targetPlayer, String uuid, int type) {
        String sql = "INSERT INTO op_change_log (text, command_sender, target_player, uuid, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, text);
            stmt.setString(2, commandSender);
            stmt.setString(3, targetPlayer);
            stmt.setString(4, uuid);
            stmt.setInt(5, type);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logWarning(String text, String player) {
        String sql = "INSERT INTO warning_log (text, player) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, text);
            stmt.setString(2, player);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logKeywordsAlert(String text, String command, String player) {
        String sql = "INSERT INTO keywords_alert_log (text, command, player) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, text);
            stmt.setString(2, command);
            stmt.setString(3, player);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}