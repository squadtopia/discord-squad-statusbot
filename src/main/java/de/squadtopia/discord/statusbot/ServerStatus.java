package de.squadtopia.discord.statusbot;

class ServerStatus {

    private String messageId;
    private long lastUpdated;

    public boolean isExpired() {
        return lastUpdated == 0 || messageId == null || System.currentTimeMillis() - lastUpdated >= ServerMessageListener.DELAY_IN_MS;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getMessageId() {
        return messageId;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
