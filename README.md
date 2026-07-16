# FastAIMemory 0.1.3 — Unified Conversation History and Memory Orchestration for Java

[![Status](https://img.shields.io/badge/status-0.1.3-brightgreen.svg)](https://github.com/andrestubbe/FastAIMemory/releases/tag/0.1.3)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

---

**💡 Extremely lightweight, provider-independent, thread-safe conversation history, formatters, and memory-trimming utilities for the FastJava AI Ecosystem.**

FastAIMemory is a **primitive context manager** for Java. It stores conversation messages, optimizes history boundaries (sliding windows, character limits, token estimates), and converts structural data into target-formatted prompt strings (ChatML, Gemini, Claude) to keep LLM context clean and safe.

---

## Quick Start

```java
import fastaimemory.*;

public class Demo {
    public static void main(String[] args) {
        // 1. Initialize thread-safe conversation history
        ConversationHistory history = new ConversationHistory();
        history.system("You are a helpful coding assistant.");
        
        history.user("Hello!");
        history.assistant("Hi! How can I help you today?");
        
        history.user("Write a quicksort in Java:");

        // 2. Format history dynamically for different providers
        MemoryContextBuilder builder = new MemoryContextBuilder(new ChatMLFormatter());
        String chatMLPrompt = builder.build(history);
        System.out.println(chatMLPrompt);
    }
}
```

---

## Why FastAIMemory?

- **Provider Agnostic** — Decoupled from specific provider APIs. Works seamlessly with OpenAI, Gemini, Claude, Ollama, and local runtimes.
- **Thread Safe** — Thread-safe `ConversationHistory` using lock synchronization makes it reliable for concurrent multi-agent environments.
- **Polymorphic Formatters** — Implement `MemoryFormatter` to structure output prompts dynamically using plain text, ChatML, or specialized provider tokens.
- **Zero Dependencies** — Built on pure Java 17, keeping compiling speeds lighting fast and jar footprint zero-bloat.

---

## Installation

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastAIMemory</artifactId>
        <version>0.1.3</version>
    </dependency>
</dependencies>
```

---

## API Reference

### `ConversationHistory`
Thread-safe container storing conversation turns.
- `add(ConversationRole role, String text)`: Appends a raw message.
- `system(String text)` / `user(String text)` / `assistant(String text)`: Helper methods.
- `messages()`: Returns a read-only list copy of the messages.
- `clear()`: Wipes history.

### `MemoryWindow`
Utility for trimming historical context prior to sending requests:
- `trimToMessages(List<ConversationMessage> messages, int maxMessages)`: Retains the first `SYSTEM` message, keeping only the latest $N$ messages.
- `trimToCharacters(List<ConversationMessage> messages, int maxChars)`: Trims oldest messages until text length fits within limits.
- `trimToEstimatedTokens(List<ConversationMessage> messages, int maxTokens)`: Uses a $chars / 4$ heuristic estimation for context pruning.

---

## Related Projects

- [FastAI](https://github.com/andrestubbe/FastAI) - Unified AI client interface for Java
- [FastAIModel](https://github.com/andrestubbe/FastAIModel) - Native local inference runtime (GGUF/ONNX)
- [FastCore](https://github.com/andrestubbe/FastCore) - Unified JNI loader and platform abstraction

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
