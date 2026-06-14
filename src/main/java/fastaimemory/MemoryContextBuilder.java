package fastaimemory;

import java.util.List;

public final class MemoryContextBuilder {
    private final MemoryFormatter formatter;

    public MemoryContextBuilder(MemoryFormatter formatter) {
        this.formatter = formatter;
    }

    public String build(ConversationHistory history) {
        if (history == null) return "";
        return build(history.messages());
    }

    public String build(List<ConversationMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        return formatter.format(messages);
    }
}
