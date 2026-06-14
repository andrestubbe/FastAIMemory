package fastaimemory;

import java.util.List;

public final class ChatMLFormatter implements MemoryFormatter {
    @Override
    public String format(List<ConversationMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ConversationMessage m : messages) {
            String roleName = switch (m.role()) {
                case SYSTEM -> "system";
                case USER -> "user";
                case ASSISTANT -> "assistant";
            };
            sb.append("<|im_start|>").append(roleName).append("\n")
              .append(m.text()).append("<|im_end|>\n");
        }
        return sb.toString();
    }
}
