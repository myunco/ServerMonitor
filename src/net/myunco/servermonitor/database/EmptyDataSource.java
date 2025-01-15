package net.myunco.servermonitor.database;

public class EmptyDataSource implements DataSource {
    @Override
    public void logChat(String text, String player, String uuid) {

    }

    @Override
    public void logCommand(String text, String command, String player, String uuid, boolean isOp) {

    }

    @Override
    public void logGameModeChange(String text, String gamemode, String player, String uuid) {

    }

    @Override
    public void logJoinLeave(String text, String player, String ip, String kickReason) {

    }

    @Override
    public void logOpChange(String text, String commandSender, String targetPlayer, String uuid, int type) {

    }

    @Override
    public void logWarning(String text, String player) {

    }

    @Override
    public void logKeywordsAlert(String text, String command, String player) {

    }

    @Override
    public void closeConnection() {

    }
}
