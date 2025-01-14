package net.myunco.servermonitor.database;

public interface DataSource {
    void logChat(String text, String player, String uuid);
    void logCommand(String text, String command, String player, String uuid, boolean isOp);
    void logGameModeChange(String text, String gamemode, String player, String uuid);
    void logJoinLeave(String text, String player, String ip, String kickReason);
    void logOpChange(String text, String commandSender, String targetPlayer, String uuid, int type);
    void logWarning(String text, String player);
    void logKeywordsAlert(String text, String command, String player);
}