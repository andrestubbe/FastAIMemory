# FastAIMemory 0.1.4 — Unified Conversation History and Memory Orchestration for Java

[![Status](https://img.shields.io/badge/status-0.1.4-brightgreen.svg)](https://github.com/andrestubbe/FastAIMemory/releases/tag/0.1.4)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

---

**💡 Extremely lightweight, provider-independent, thread-safe conversation history, formatters, and memory-trimming utilities for the FastJava AI Ecosystem.**

FastAIMemory is a **primitive context manager** for Java. It unifies all 3 core AI memory pattern families behind a clean, zero-bloat interface:
1. **Window Memory** (MemoryWindow) — Sliding message, character, and token windows.
2. **Summary Memory** (SummaryMemory) — Rolling background condensation of aging chat turns.
3. **Semantic Memory** (SemanticMemory) — Dynamic context recall based on similarity or keywords.

---

## Quick Start

`java
import fastaimemory.*;

public class Demo {
    public static void main(String[] args) {
        // 1. Sliding Window & History
        ConversationHistory history = new ConversationHistory();
        history.system("You are a helpful coding assistant.");
        history.user("Hello!");
        history.assistant("Hi! How can I help you today?");

        // 2. Summary Memory (Compacts aging dialogue)
        SummaryMemory summaryMem = new SummaryMemory(4, rawText -> "User asked about Java coding.");
        summaryMem.system("You are an expert engineer.");
        summaryMem.user("How do I implement quicksort?");

        // 3. Semantic Memory (Recalls relevant snippets)
        SemanticMemory semanticMem = new SemanticMemory(3, null);
        semanticMem.remember("pref_java", "User prefers Java 17+ and zero-dependency libraries.");
        var recalled = semanticMem.recall("Show me Java code");
    }
}
`

---

## Table of Contents

- [Why FastAIMemory?](#why-fastaimemory)
- [Key Features](#key-features)
- [Installation](#installation)
- [API Reference](#api-reference)
- [Memory Patterns Supported](#memory-patterns-supported)
- [Performance Benchmarks](#performance-benchmarks)
- [Technical Examples & Demos](#technical-examples--demos)
- [API Quick Reference](#api-quick-reference)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastAIMemory?

Current memory solutions in Java are deeply tied to bloated frameworks and heavy ORMs.

FastAIMemory solves this by providing:

- **3 Core Memory Patterns** — Full support for Window Memory, Rolling Summary Memory, and Semantic Recall.
- **Provider Agnostic** — Decoupled from specific provider APIs. Works seamlessly with OpenAI, Gemini, Claude, Ollama, and local runtimes.
- **Thread Safe** — Thread-safe ConversationHistory using lock synchronization makes it reliable for concurrent multi-agent environments.
- **Polymorphic Formatters** — Implement MemoryFormatter to structure output prompts dynamically using plain text, ChatML, or specialized provider tokens.
- **Zero Dependencies** — Pure Java 17+, no Jackson, no Spring, no heavy third-party drivers.

---

## Key Features

- **🪟 Sliding Window Pruning** — Instant deterministic context trimming by message counts, character limits, or heuristic token estimates.
- **🧠 Rolling Summary Memory** — Automatic condensation of older conversation turns while keeping recent context and system prompts active.
- **🔍 Semantic Memory Recall** — Fast retrieval of user preferences and relevant knowledge snippets into active prompts.
- **🎭 Unified Formatters** — Built-in polymorphic formatters for ChatML (<|im_start|>), Claude, Gemini, and plain text.
- **⚡ Ultra-Lightweight** — Zero allocations on hot-paths with sub-microsecond formatting throughput.

---

## Installation

Add the JitPack repository and the dependency to your pom.xml:

`xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<!-- FastAIMemory Library -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>FastAIMemory</artifactId>
    <version>0.1.4</version>
</dependency>
</dependencies>
`

---

## API Reference

### History & Windows

`java
// Thread-safe Conversation History
ConversationHistory history = new ConversationHistory();
history.system("You are a Java engineer.");
history.user("Explain memory models.");
history.assistant("Java uses JMM...");

// Sliding Window Trimming
List<ConversationMessage> trimmed = MemoryWindow.trimToMessages(history.messages(), 10);
`

### Rolling Summary Memory

`java
// Compacts history when turns exceed threshold
SummaryMemory summaryMem = new SummaryMemory(6, rawDialogue -> {
    return "User is discussing concurrency and zero-allocation pipelines.";
});
summaryMem.user("How do I eliminate allocations?");
`

### Semantic Memory Recall

`java
// Stores and recalls relevant context
SemanticMemory semanticMem = new SemanticMemory(3, null);
semanticMem.remember("arch_goal", "Target 60+ FPS zero GC in timeline orchestration.");
var results = semanticMem.recall("FPS timeline");
`

---

## Memory Patterns Supported

| Pattern Family | Mechanism | Primary Class | Best Use Case |
|---|---|---|---|
| **Window Memory** | Sliding message, character & token window | MemoryWindow | Real-time chat loops, short interactive sessions |
| **Summary Memory** | Rolling LLM-assisted context condensation | SummaryMemory | Long-running agent execution, task chains |
| **Semantic Memory** | Relevance & similarity-based recall | SemanticMemory | User preferences, long-term memory, knowledge facts |

---

## Performance Benchmarks

FastAIMemory is rigorously profiled using **JMH** to guarantee zero-overhead memory pruning and formatting:

| Metric / Hot-Path Operation | Score (ops/ms) | Ops per Second |
|-----------------------------|----------------|----------------|
| **Sliding Window Trimming** | ~16,410 ops/ms | > 16.4 Million |
| **Gemini Prompt Formatting** | ~657 ops/ms   | > 657,000 ops/sec |
| **ChatML Prompt Formatting** | ~470 ops/ms   | > 470,000 ops/sec |

*Measured on Windows 11, Intel Core i5-1135G7 (Surface Pro 8), JDK 21.0.12. Measures full message chain transformations and sliding array operations.*

### Framework Comparison

FastAIMemory is **zero-dependency** and **zero-allocation** for core orchestration:

| Metric              | LangChain4j Memory | Spring AI Memory | FastAIMemory  |
|---------------------|--------------------|------------------|---------------|
| **Dependencies**    | 10+                | 15+              | **0**         |
| **JAR Size**        | ~2MB               | ~4MB             | **~20KB**     |
| **Startup Time**    | 1-2s               | 3-5s             | **<10ms**     |
| **Memory Overhead** | High               | High             | **Minimal**   |
| **Learning Curve**  | Hours              | Hours            | **2 minutes** |

---

## Technical Examples & Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Memory Orchestration Demo** | [Demo.java](src/test/java/fastaimemory/Demo.java) | un-demo.bat | Interactive CLI demo showcasing Sliding Window, Rolling Summaries, and Semantic Memory. |
| **JMH Microbenchmarks** | [FastAIMemoryBenchmark.java](examples/Benchmark/src/main/java/fastaimemory/FastAIMemoryBenchmark.java) | un-benchmark.bat | JMH throughput benchmark for ChatML/Gemini prompt formatting and memory trimming. |

---

## API Quick Reference

| Method / Class | Return Type | Description |
|----------------|-------------|-------------|
| history.add(role, text) | oid | Appends a raw conversation message turn. |
| history.messages() | List<ConversationMessage> | Returns a thread-safe read-only view of current turns. |
| MemoryWindow.trimToMessages(list, n) | List<ConversationMessage> | Retains system prompt and latest $ messages. |
| summaryMem.messages() | List<ConversationMessage> | Returns condensed summary combined with recent turns. |
| semanticMem.recall(query) | List<MemoryEntry> | Recalls top matching knowledge snippets. |

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | 🚧 Planned        |
| macOS         | 🚧 Planned        |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastAI](https://github.com/andrestubbe/FastAI) — Unified AI client interface for Java
- [FastAIModel](https://github.com/andrestubbe/FastAIModel) — Native local inference runtime (GGUF/ONNX)
- [FastCore](https://github.com/andrestubbe/FastCore) — Unified JNI loader and platform abstraction

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
