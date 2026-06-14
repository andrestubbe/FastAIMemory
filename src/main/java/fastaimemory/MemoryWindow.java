package fastaimemory;

import java.util.ArrayList;
import java.util.List;

public final class MemoryWindow {

    public static List<ConversationMessage> trimToMessages(List<ConversationMessage> all, int maxMessages) {
        if (all == null || all.isEmpty() || maxMessages <= 0) {
            return new ArrayList<>();
        }
        if (all.size() <= maxMessages) {
            return new ArrayList<>(all);
        }

        List<ConversationMessage> trimmed = new ArrayList<>();
        ConversationMessage systemMsg = null;
        int startIndex = 0;

        if (all.get(0).role() == ConversationRole.SYSTEM) {
            systemMsg = all.get(0);
            startIndex = 1;
        }

        int limit = systemMsg != null ? maxMessages - 1 : maxMessages;
        int totalNonSystem = all.size() - startIndex;

        if (systemMsg != null) {
            trimmed.add(systemMsg);
        }

        int skip = totalNonSystem - limit;
        if (skip < 0) skip = 0;
        for (int i = startIndex + skip; i < all.size(); i++) {
            trimmed.add(all.get(i));
        }

        return trimmed;
    }

    public static List<ConversationMessage> trimToCharacters(List<ConversationMessage> all, int maxChars) {
        if (all == null || all.isEmpty() || maxChars <= 0) {
            return new ArrayList<>();
        }

        List<ConversationMessage> trimmed = new ArrayList<>();
        ConversationMessage systemMsg = null;
        int startIndex = 0;

        if (all.get(0).role() == ConversationRole.SYSTEM) {
            systemMsg = all.get(0);
            startIndex = 1;
        }

        int currentChars = 0;
        if (systemMsg != null) {
            currentChars += systemMsg.text().length();
        }

        List<ConversationMessage> temp = new ArrayList<>();
        for (int i = all.size() - 1; i >= startIndex; i--) {
            ConversationMessage m = all.get(i);
            if (currentChars + m.text().length() > maxChars) {
                break;
            }
            temp.add(0, m);
            currentChars += m.text().length();
        }

        if (systemMsg != null) {
            trimmed.add(systemMsg);
        }
        trimmed.addAll(temp);
        return trimmed;
    }

    public static List<ConversationMessage> trimToEstimatedTokens(List<ConversationMessage> all, int maxTokens) {
        return trimToCharacters(all, maxTokens * 4);
    }
}
