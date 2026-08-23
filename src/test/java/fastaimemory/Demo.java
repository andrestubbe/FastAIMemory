package fastaimemory;

import fastai.AI;
import fastai.FastAI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Interactive Memory Showcase Demo:
 * Demonstrates Window Memory, Rolling Summary Memory, and Semantic Memory in action.
 */
public class Demo {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("     🧠 FastAIMemory — Unified AI Memory Architecture Demo    ");
        System.out.println("===============================================================");
        System.out.println("1. Window Memory   (Sliding message/token context)");
        System.out.println("2. Summary Memory  (Auto-condensing rolling summaries)");
        System.out.println("3. Semantic Memory (Fast recall of preferences & facts)");
        System.out.println("===============================================================\n");

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        try {
            // Optional FastAI integration if user wants live LLM generation
            AI ai = null;
            final String apiKey = System.getenv("GROQ_API_KEY") != null ? System.getenv("GROQ_API_KEY") : System.getenv("OPENAI_API_KEY");
            if (apiKey != null) {
                try {
                    ai = System.getenv("GROQ_API_KEY") != null
                            ? FastAI.connect("groq:llama-3.3-70b-versatile", apiKey)
                            : FastAI.connect("openai:gpt-4o-mini", apiKey);
                    System.out.println("✅ Live LLM Connected for real-time memory synthesis.");
                } catch (final Exception ignored) {
                }
            }

            final ConversationHistory history = new ConversationHistory();
            history.system("You are a high-performance Java coding assistant.");

            final SemanticMemory semanticMemory = new SemanticMemory(2, null);
            semanticMemory.remember("pref_style", "User prefers Java 17+, zero external dependencies, and strict performance.");
            semanticMemory.remember("pref_naming", "User uses 'final' and 'this.' everywhere for maximum clarity.");

            final SummaryMemory summaryMemory = new SummaryMemory(4, rawDialogue -> {
                System.out.println("\n[🧠 Compacting dialogue into rolling summary...]");
                return "User is discussing FastJava memory architecture, zero-allocation principles, and JMH benchmarking.";
            });

            System.out.println("💡 Interactive chat ready. Type your messages (or 'exit' to quit):\n");

            while (true) {
                System.out.print("User > ");
                final String input = reader.readLine();
                if (input == null || input.trim().equalsIgnoreCase("exit")) {
                    break;
                }

                // 1. Semantic Recall
                final var recalled = semanticMemory.recall(input);
                if (!recalled.isEmpty()) {
                    System.out.println("  🔍 [Semantic Recall] Attached fact: " + recalled.get(0).content());
                }

                // 2. Add to histories
                history.user(input);
                summaryMemory.user(input);

                // 3. Format context using ChatML
                final MemoryContextBuilder builder = new MemoryContextBuilder(new ChatMLFormatter());
                final String chatML = builder.build(history);

                System.out.println("  📄 [ChatML Tokens] " + history.size() + " turns, length: " + chatML.length() + " chars");

                // 4. Response simulation or live AI
                if (ai != null) {
                    System.out.print("Assistant > ");
                    ai.stream(chatML, token -> {
                        System.out.print(token);
                        System.out.flush();
                    });
                    System.out.println();
                } else {
                    final String mockReply = "Processed turn with " + history.size() + " messages in context.";
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
