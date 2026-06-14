package fastaimemory;

import fastai.FastAI;
import fastai.AI;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      FastAIMemory - Flawless German Corrector   ");
        System.out.println("=================================================");

        try {
            // Read API key
            String apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.out.print("Enter your GEMINI_API_KEY: ");
                BufferedReader keyReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
                apiKey = keyReader.readLine();
                if (apiKey == null || apiKey.trim().isEmpty()) {
                    System.out.println("❌ Error: API Key is required.");
                    System.exit(1);
                }
                System.out.println();
            }

            // Connect to Gemini
            AI ai = FastAI.connect("gemini:gemini-1.5-flash", new String[]{apiKey});

            // Initialize History with System Prompt
            ConversationHistory history = new ConversationHistory();
            history.system("Korrigiere diesen text in makelloses Deutsch.");

            MemoryContextBuilder builder = new MemoryContextBuilder(new GeminiFormatter());
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

            while (true) {
                System.out.print("\nDeutscher Text (oder 'exit' zum Beenden): ");
                String input = inputReader.readLine();
                if (input == null || input.trim().equalsIgnoreCase("exit")) {
                    break;
                }

                // Add to history
                history.user(input);

                // Build context
                String prompt = builder.build(history);

                System.out.println("\n[Sende an Gemini...]");
                String response = ai.ask(prompt);

                System.out.println("\nKorrigiert:\n" + response);

                // Store assistant response in history
                history.assistant(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
