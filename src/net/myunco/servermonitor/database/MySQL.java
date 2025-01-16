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
    private Connection connection;
    private String logChatSql;
    private String logCommandSql;
    private String logGamemodeSql;
    private String logJoinLeaveSql;
    private String logOpChangeSql;
    private String logWarningSql;
    private String logKeywordSql;

    private MySQL() throws SQLException {
        initDataSource();
    }

    private void initDataSource() throws SQLException {
        String prefix = Config.dbTablePrefix.isEmpty() ? "" : Config.dbTablePrefix;
        String chatTableName = prefix + "chat_log";
        String commandTableName = prefix + "command_log";
        String gamemodeTableName = prefix + "gamemode_log";
        String joinLeaveTableName = prefix + "join_leave_log";
        String opChangeTableName = prefix + "op_change_log";
        String warningTableName = prefix + "warning_log";
        String keywordTableName = prefix + "keywords_alert_log";

        logChatSql = "INSERT INTO `" + chatTableName + "` (text, player, uuid) VALUES (?, ?, ?)";
        logCommandSql = "INSERT INTO `" + commandTableName + "` (text, command, command_sender, uuid, op) VALUES (?, ?, ?, ?, ?)";
        logGamemodeSql = "INSERT INTO `" + gamemodeTableName + "` (text, gamemode, player, uuid) VALUES (?, ?, ?, ?)";
        logJoinLeaveSql = "INSERT INTO `" + joinLeaveTableName + "` (text, player, ip, kick_reason) VALUES (?, ?, ?, ?)";
        logOpChangeSql = "INSERT INTO `" + opChangeTableName + "` (text, command_sender, target_player, uuid, type) VALUES (?, ?, ?, ?, ?)";
        logWarningSql = "INSERT INTO `" + warningTableName + "` (text, player) VALUES (?, ?)";
        logKeywordSql = "INSERT INTO `" + keywordTableName + "` (text, command, player) VALUES (?, ?, ?)";
        connection("jdbc:mysql://" + Config.dbHost + ":" + Config.dbPort + "/?useSSL=false&serverTimezone=UTC");
        Statement stat = connection.createStatement();
        stat.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + Config.dbName + "` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci'");
        stat.executeUpdate("USE `" + Config.dbName + "`");
        stat.close();
        //region 建表语句
        String[] createTableSQL = {
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
                        "  `command_sender` varchar(255) NOT NULL," +
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
        //endregion
        for (String sql : createTableSQL) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        }
    }

    public static DataSource getInstance() { //不是单例模式
        try {
            return new MySQL();
        } catch (SQLException e) {
            plugin.getLogger().severe("连接数据库失败或初始化错误：" + e.getLocalizedMessage());
            e.printStackTrace();
            // 调用方严格检查null 因此返回null是可靠的 不会造成空指针异常
            return null;
        }
    }

    private void connection(String jdbcUrl) throws SQLException {
        connection = DriverManager.getConnection(jdbcUrl, Config.dbUsername, Config.dbPassword);
        plugin.logMessage("连接数据库成功");
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (connection != null) {
                plugin.getLogger().warning("数据库连接被意外关闭，将尝试重新连接。（此情况不应发生，如果你频繁看到此警告，请排除不稳定因素）");
            }
            connection("jdbc:mysql://" + Config.dbHost + ":" + Config.dbPort + "/" + Config.dbName + "?useSSL=false&serverTimezone=UTC");
        }
        return connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                plugin.getLogger().severe("关闭数据库连接失败");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void logChat(String text, String player, String uuid){
        executeUpdate(logChatSql, text, player, uuid);
    }

    @Override
    public void logCommand(String text, String command, String player, String uuid, boolean isOp) {
        executeUpdate(logCommandSql, text, command, player, uuid, isOp ? "YES" : "NO");
    }

    @Override
    public void logGameModeChange(String text, String gamemode, String player, String uuid) {
        executeUpdate(logGamemodeSql, text, gamemode, player, uuid);
    }

    @Override
    public void logJoinLeave(String text, String player, String ip, String kickReason) {
        executeUpdate(logJoinLeaveSql, text, player, ip, kickReason);
    }

    @Override
    public void logOpChange(String text, String commandSender, String targetPlayer, String uuid, int type) {
        try (PreparedStatement stmt = getConnection().prepareStatement(logOpChangeSql)) {
            stmt.setString(1, text);
            stmt.setString(2, commandSender);
            stmt.setString(3, targetPlayer);
            stmt.setString(4, uuid);
            stmt.setInt(5, type);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("向数据库中添加OP修改日志失败：" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void logWarning(String text, String player) {
        executeUpdate(logWarningSql, text, player);
    }

    @Override
    public void logKeywordsAlert(String text, String command, String player) {
        executeUpdate(logKeywordSql, text, command, player);
    }

    private void executeUpdate(String sql, String... params) {
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("执行数据库更新操作失败：" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}