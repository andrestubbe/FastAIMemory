package fastaimemory;

import java.util.List;

public final class GeminiFormatter implements MemoryFormatter {
    @Override
    public String format(List<ConversationMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ConversationMessage m : messages) {
            switch (m.role()) {
                case SYSTEM -> sb.append("System: ").append(m.text()).append("\n\n");
                case USER -> sb.append("user: ").append(m.text()).append("\n");
                case ASSISTANT -> sb.append("model: ").append(m.text()).append("\n");
            }
        }
        return sb.toString();
    }
}
