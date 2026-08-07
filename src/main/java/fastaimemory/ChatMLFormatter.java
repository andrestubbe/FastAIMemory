package fastaimemory;

import java.util.List;

/**
 * Formats conversation history using ChatML tokens (<|im_start|> / <|im_end|>).
 * Required for local models like Qwen2.5, Mistral, etc. that use ChatML chat templates.
 */
public final class ChatMLFormatter implements MemoryFormatter {
    @Override
    public String format(List<ConversationMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ConversationMessage m : messages) {
            String role = switch (m.role()) {
                case SYSTEM    -> "system";
                case USER      -> "user";
                case ASSISTANT -> "assistant";
            };
            sb.append("<|im_start|>").append(role).append("\n")
              .append(m.text())
              .append("<|im_end|>\n");
        }
        sb.append("<|im_start|>assistant\n");
        return sb.toString();
    }
}
