package fastaimemory;

import java.util.List;

public final class PlainTextFormatter implements MemoryFormatter {
    @Override
    public String format(List<ConversationMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ConversationMessage m : messages) {
            String roleName = switch (m.role()) {
                case SYSTEM -> "System Instruction";
                case USER -> "User";
                case ASSISTANT -> "You";
            };
            sb.append(roleName).append(": ").append(m.text()).append("\n\n");
        }
        sb.append("You: ");
        return sb.toString();
    }
}
