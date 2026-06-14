package fastaimemory;

import java.util.List;

public final class PlainFormatter implements MemoryFormatter {
    @Override
    public String format(List<ConversationMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ConversationMessage m : messages) {
            switch (m.role()) {
                case SYSTEM -> sb.append("System: ").append(m.text()).append("\n");
                case USER -> sb.append("User: ").append(m.text()).append("\n");
                case ASSISTANT -> sb.append("Assistant: ").append(m.text()).append("\n");
            }
        }
        return sb.toString();
    }
}
