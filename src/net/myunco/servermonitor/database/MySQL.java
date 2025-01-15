package net.myunco.servermonitor.database;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL implements DataSource {
    public static ServerMonitor plugin = ServerMonitor.getPlugin();
    private Connection conn;
    private String chatTableName = "chat_log";
    private String commandTableName = "command_log";
    private String gamemodeTableName = "gamemode_log";
    private String joinLeaveTableName = "join_leave_log";
    private String opChangeTableName = "op_change_log";
    private String warningTableName = "warning_log";
    private String keywordTableName = "keywords_alert_log";
    private final String[] createTableSQL = {
            "CREATE TABLE IF NOT EXISTS `" + chatTableName + "` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(4096) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

            "CREATE TABLE IF NOT EXISTS `" + commandTableName + "` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(4096) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `command` varchar(4096) NOT NULL," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  `op` char(3) NOT NULL DEFAULT 'NO'," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

            "CREATE TABLE IF NOT EXISTS `" + gamemodeTableName + "` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `gamemode` varchar(10) NOT NULL," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

            "CREATE TABLE IF NOT EXISTS `" + joinLeaveTableName + "` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `player` varchar(255) NOT NULL," +
                    "  `ip` varchar(22) NOT NULL," +
                    "  `kick_reason` varchar(256)," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

            "CREATE TABLE IF NOT EXISTS `" + opChangeTableName + "` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `command_sender` varchar(255) NOT NULL COMMENT '操作源'," +
                    "  `target_player` char(255) NOT NULL," +
                    "  `uuid` char(36) NOT NULL," +
                    "  `type` tinyint(1) NOT NULL COMMENT '操作类型 0=撤销OP权限 1=授予OP权限'," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

            "CREATE TABLE IF NOT EXISTS `" + warningTableName + "` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `player` varchar(255) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

            "CREATE TABLE IF NOT EXISTS `" + keywordTableName + "` (" +
                    "  `id` int NOT NULL AUTO_INCREMENT," +
                    "  `text` varchar(1024) NOT NULL," +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  `command` varchar(1024) NOT NULL," +
                    "  `player` varchar(255) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;"
    };


    private MySQL() throws SQLException {
        init();
        getConnection();
    }

    public void init() {
        if (!Config.dbTablePrefix.isEmpty()) {
            chatTableName = Config.dbTablePrefix + chatTableName;
            commandTableName = Config.dbTablePrefix + commandTableName;
            gamemodeTableName = Config.dbTablePrefix + gamemodeTableName;
            joinLeaveTableName = Config.dbTablePrefix + joinLeaveTableName;
            opChangeTableName = Config.dbTablePrefix + opChangeTableName;
            warningTableName = Config.dbTablePrefix + warningTableName;
            keywordTableName = Config.dbTablePrefix + keywordTableName;
        }
    }
    public static DataSource getInstance() {
        try {
            return new MySQL();
        } catch (SQLException e) {
            plugin.getLogger().severe("连接数据库失败或初始化错误！");
            e.printStackTrace();
            return new EmptyDataSource();
        }
    }

    public Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            System.out.println("执行了一次" + conn);
            String jdbcUrl = "jdbc:mysql://" + Config.dbHost + ":" + Config.dbPort + "/?useSSL=false&serverTimezone=UTC";
            conn = DriverManager.getConnection(jdbcUrl, Config.dbUsername, Config.dbPassword);
            plugin.logMessage("连接数据库成功");
            Statement stat = conn.createStatement();
            stat.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + Config.dbName + "` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci'");
            stat.executeUpdate("USE `" + Config.dbName + "`");
            stat.close();

            conn.setAutoCommit(false);

            for (String sql : createTableSQL) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        }
        return conn;
    }

    @Override
    public void closeConnection() {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
            conn = null;
        } catch (SQLException ignored) {
        }
    }

    @Override
    public void logChat(String text, String player, String uuid){
        String sql = "INSERT INTO `" + chatTableName + "` (text, player, uuid) VALUES (?, ?, ?)";
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
        String sql = "INSERT INTO `" + commandTableName + "` (text, command, player, uuid, op) VALUES (?, ?, ?, ?, ?)";
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
        String sql = "INSERT INTO `" + gamemodeTableName + "` (text, gamemode, player, uuid) VALUES (?, ?, ?, ?)";
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
        String sql = "INSERT INTO `" + joinLeaveTableName + "` (text, player, ip, kick_reason) VALUES (?, ?, ?, ?)";
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
        String sql = "INSERT INTO `" + opChangeTableName + "` (text, command_sender, target_player, uuid, type) VALUES (?, ?, ?, ?, ?)";
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
        String sql = "INSERT INTO `" + warningTableName + "` (text, player) VALUES (?, ?)";
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
        String sql = "INSERT INTO `" + keywordTableName + "` (text, command, player) VALUES (?, ?, ?)";
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