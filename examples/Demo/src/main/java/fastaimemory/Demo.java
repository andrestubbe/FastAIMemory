package fastaimemory;

import fastai.AI;
import fastai.FastAI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Complete FastAIMemory Architecture Showcase:
 * Interactively demonstrates all memory types and formatting utilities.
 */
public class Demo {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("   🧠 FastAIMemory — Unified AI Memory & Context Architecture Showcase  ");
        System.out.println("=========================================================================");
        System.out.println(" Demonstrates: ");
        System.out.println("   1. Window Memory   (Sliding message, character, and token limits)");
        System.out.println("   2. Summary Memory  (Auto-condensing rolling summaries with LLM)");
        System.out.println("   3. Semantic Memory (Similarity recall of user profile and facts)");
        System.out.println("   4. Formatters      (Polymorphic ChatML, Gemini, Claude, and Plain)");
        System.out.println("=========================================================================\n");

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        try {
            // Check for available FastAI connection for live LLM streaming
            AI ai = null;
            final String apiKey = System.getenv("GROQ_API_KEY") != null ? System.getenv("GROQ_API_KEY") : System.getenv("OPENAI_API_KEY");
            if (apiKey != null) {
                try {
                    ai = System.getenv("GROQ_API_KEY") != null
                            ? FastAI.connect("groq:llama-3.3-70b-versatile", apiKey)
                            : FastAI.connect("openai:gpt-4o-mini", apiKey);
                    System.out.println("✨ [FastAI Connected] Live LLM streaming active for memory synthesis.\n");
                } catch (final Exception ignored) {
                }
            }

            // 1. Initialize Conversation History
            final ConversationHistory history = new ConversationHistory();
            history.system("You are an expert high-performance Java systems architect.");

            // 2. Initialize Semantic Memory with user facts
            final SemanticMemory semanticMemory = new SemanticMemory(2, null);
            semanticMemory.remember("pref_runtime", "User runs Java 17+, zero-dependency libraries with pure native speed.");
            semanticMemory.remember("pref_style", "User enforces strict 'final' and 'this.' everywhere for clean code.");
            semanticMemory.remember("bench_target", "Target throughput > 10M ops/sec with 0 heap allocation on hot-paths.");

            // 3. Initialize Rolling Summary Memory
            final SummaryMemory summaryMemory = new SummaryMemory(4, rawDialogue -> {
                System.out.println("\n  [🧠 Summary Engine] Compacting older conversation turns...");
                return "User and Assistant established FastJava zero-allocation guidelines and verified JMH microbenchmarks.";
            });

            // 4. Formatters
            final MemoryContextBuilder chatMLBuilder = new MemoryContextBuilder(new ChatMLFormatter());
            final MemoryContextBuilder geminiBuilder = new MemoryContextBuilder(new GeminiFormatter());
            final MemoryContextBuilder claudeBuilder = new MemoryContextBuilder(new ClaudeFormatter());

            System.out.println("💬 Interactive Console Ready. Type a prompt (e.g. 'Show me Java guidelines' or 'exit'):\n");

            while (true) {
                System.out.print("User > ");
                final String input = reader.readLine();
                if (input == null || input.trim().equalsIgnoreCase("exit")) {
                    break;
                }

                // A. Semantic Memory Recall
                final List<SemanticMemory.MemoryEntry> recalled = semanticMemory.recall(input);
                if (!recalled.isEmpty()) {
                    System.out.println("\n  🔍 [Semantic Recall] Found " + recalled.size() + " relevant memory items:");
                    for (final SemanticMemory.MemoryEntry entry : recalled) {
                        System.out.println("     • " + entry.content());
                    }
                }

                // B. Append to History & Summary Memory
                history.user(input);
                summaryMemory.user(input);

                // C. Window Memory Pruning preview
                final List<ConversationMessage> windowTrimmed = MemoryWindow.trimToMessages(history.messages(), 4);
                final List<ConversationMessage> tokenTrimmed = MemoryWindow.trimToEstimatedTokens(history.messages(), 256);

                System.out.println("\n  🪟 [Window Memory] Total Turns: " + history.size() + 
                                   " | Window(4): " + windowTrimmed.size() + 
                                   " | TokenLimit(256 tokens): " + tokenTrimmed.size());

                // D. Show Formatting output lengths
                final String chatML = chatMLBuilder.build(history);
                final String gemini = geminiBuilder.build(history);
                final String claude = claudeBuilder.build(history);

                System.out.println("  🎭 [Formatters] ChatML: " + chatML.length() + " chars | Gemini: " + gemini.length() + " chars | Claude: " + claude.length() + " chars\n");

                // E. Assistant Generation (Live Streaming or simulated response)
                if (ai != null) {
                    System.out.print("Assistant > ");
                    final StringBuilder responseBuffer = new StringBuilder();
                    ai.stream(chatML, token -> {
                        System.out.print(token);
                        System.out.flush();
                        responseBuffer.append(token);
                    });
                    System.out.println();
                    history.assistant(responseBuffer.toString());
                    summaryMemory.assistant(responseBuffer.toString());
                } else {
                    final String mockReply = "Executed turn #" + history.size() + ". All 3 memory patterns and 4 formatters active and verified.";
                    System.out.println("Assistant > " + mockReply);
                    history.assistant(mockReply);
                    summaryMemory.assistant(mockReply);
                }
                System.out.println();
            }

        } catch (final Exception e) {
            System.err.println("❌ Demo Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
