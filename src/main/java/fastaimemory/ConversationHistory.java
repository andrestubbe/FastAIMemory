package fastaimemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConversationHistory {
    private final List<ConversationMessage> messages = new ArrayList<>();

    public synchronized void add(ConversationRole role, String text) {
        messages.add(new ConversationMessage(role, text));
    }

    public synchronized void system(String text) {
        add(ConversationRole.SYSTEM, text);
    }

    public synchronized void user(String text) {
        add(ConversationRole.USER, text);
    }

    public synchronized void assistant(String text) {
        add(ConversationRole.ASSISTANT, text);
    }

    public synchronized List<ConversationMessage> messages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public synchronized void clear() {
        messages.clear();
    }

    public synchronized int size() {
        return messages.size();
    }

    public synchronized boolean isEmpty() {
        return messages.isEmpty();
    }
}
