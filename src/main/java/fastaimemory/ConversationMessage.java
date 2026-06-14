package fastaimemory;

public final class ConversationMessage {
    private final ConversationRole role;
    private final String text;
    private final long timestamp;

    public ConversationMessage(ConversationRole role, String text) {
        this.role = role;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    public ConversationRole role() {
        return role;
    }

    public String text() {
        return text;
    }

    public long timestamp() {
        return timestamp;
    }
}
