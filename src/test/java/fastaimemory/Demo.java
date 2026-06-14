package fastaimemory;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        System.out.println("Starting FastAIMemory history & formatter test...");
        System.out.println("------------------------------------------------");

        ConversationHistory history = new ConversationHistory();
        history.system("You are a helpful coding assistant.");
        
        for (int i = 1; i <= 8; i++) {
            history.user("Question number " + i);
            history.assistant("Answer number " + i);
        }

        System.out.println("Original History Size: " + history.size());

        List<ConversationMessage> trimmed = MemoryWindow.trimToMessages(history.messages(), 5);
        System.out.println("Trimmed History Size: " + trimmed.size());

        System.out.println("\n--- PlainFormatter ---");
        MemoryContextBuilder plainBuilder = new MemoryContextBuilder(new PlainFormatter());
        System.out.print(plainBuilder.build(trimmed));

        System.out.println("\n--- ChatMLFormatter ---");
        MemoryContextBuilder chatMLBuilder = new MemoryContextBuilder(new ChatMLFormatter());
        System.out.print(chatMLBuilder.build(trimmed));

        System.out.println("\n--- GeminiFormatter ---");
        MemoryContextBuilder geminiBuilder = new MemoryContextBuilder(new GeminiFormatter());
        System.out.print(geminiBuilder.build(trimmed));

        System.out.println("\n--- ClaudeFormatter ---");
        MemoryContextBuilder claudeBuilder = new MemoryContextBuilder(new ClaudeFormatter());
        System.out.println(claudeBuilder.build(trimmed));

        System.out.println("------------------------------------------------");
        System.out.println("Test completed successfully!");
    }
}
