package fastaimemory;

import java.util.List;

public final class ClaudeFormatter implements MemoryFormatter {
    @Override
    public String format(List<ConversationMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ConversationMessage m : messages) {
            switch (m.role()) {
                case SYSTEM -> sb.append("System: ").append(m.text()).append("\n\n");
                case USER -> sb.append("\n\nHuman: ").append(m.text());
                case ASSISTANT -> sb.append("\n\nAssistant: ").append(m.text());
            }
        }
        return sb.toString().trim();
    }
}
