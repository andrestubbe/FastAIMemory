# FastAIMemory 0.1.4 [ALPHA-2026-06-14]: Unified Conversation History and Memory Orchestration for Java

[![Status](https://img.shields.io/badge/status-0.1.4-brightgreen.svg)](https://github.com/andrestubbe/FastAIMemory/releases/tag/0.1.4)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-0.1.4-green.svg)](https://jitpack.io/#andrestubbe/FastAIMemory)

---

**💡 Extremely lightweight, provider-independent, thread-safe conversation history, formatters, and memory-trimming utilities for Java.**

**FastAIMemory** is a zero-bloat primitive context manager for Java. It unifies all 3 core conversational AI memory patterns behind a high-performance, allocation-minimized interface:
1. **Window Memory** (`MemoryWindow`): Sliding message, character, and token windows.
2. **Summary Memory** (`SummaryMemory`): Rolling background condensation of aging dialogue turns.
3. **Semantic Memory** (`SemanticMemory`): Fast relevance and preference recall for dynamic prompt injection.

![FastAIMemory Showcase](docs/screenshot.png)

---

## Quick Start

```java
import fastaimemory.ConversationHistory;
import fastaimemory.MemoryWindow;
import fastaimemory.SummaryMemory;
import fastaimemory.SemanticMemory;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // 1. Sliding Window & Thread-Safe Conversation History
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
        List<SemanticMemory.MemoryEntry> recalled = semanticMem.recall("Show me Java code");
    }
}
```

---

## Table of Contents

- [Why FastAIMemory?](#why-fastaimemory)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Real-World Use Cases](#real-world-use-cases)
- [Memory Patterns Supported](#memory-patterns-supported)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [API Reference](#api-reference)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [Related Projects](#related-projects)
- [License](#license)

---

## Why FastAIMemory?

Existing conversation history implementations in Java (like LangChain4j or Spring AI chat state) are heavily coupled to external database ORMs, bloated JSON serializers, and rigid prompt structures:

- **Heavyweight Framework Abstractions**: Simple message lists are often wrapped in bulky enterprise session entities, adding unnecessary latency and serialization overhead.
- **Provider Lock-In**: Many memory frameworks generate provider-specific JSON payloads directly, making it impossible to switch between OpenAI, Gemini, Claude, and local GGUF models without refactoring history classes.
- **Garbage Collection Pressure**: Continually rebuilding and reallocating context prompts in tight agent loops generates high heap churn and pauses.

FastAIMemory solves this with a provider-agnostic, zero-allocation memory substrate:

- **3 Core Memory Patterns**: Complete native support for Sliding Windows, Rolling Summaries, and Semantic Knowledge Recall in pure Java.
- **Thread-Safe & Provider-Independent**: Synchronized `ConversationHistory` compatible with any LLM driver, CLI console, or web harness.
- **Polymorphic Formatters**: High-throughput `MemoryFormatter` pipeline supporting ChatML (`<|im_start|>`), Gemini, Claude, and Markdown formats.

| Metric / Feature | LangChain4j Memory | Spring AI Memory | FastAIMemory |
|:---|:---|:---|:---|
| **Dependencies** | 10+ transitive JARs | 15+ transitive JARs | **Zero external dependencies** |
| **JAR Size** | ~2 MB | ~4 MB | **~20 KB** |
| **Startup Latency** | 1–2 seconds | 3–5 seconds | **<10 ms** |
| **Memory Patterns** | Window or external Vector | Basic Message Window | **Window + Summary + Semantic** |
| **Prompt Formatters**| Fixed vendor adapters | Fixed vendor adapters | **Polymorphic (ChatML, Gemini, Claude, Plain)** |

---

## Key Features

- 🪟 **Sliding Window Pruning**: Instant deterministic context trimming by message counts, character limits, or heuristic token estimates.
- 🧠 **Rolling Summary Memory**: Automatic condensation of older conversation turns while keeping recent turns and system prompts active.
- 🔍 **Semantic Memory Recall**: Fast retrieval of user preferences and relevant knowledge facts into active prompt context.
- 🎭 **Polymorphic Formatters**: Built-in formatters for ChatML (`<|im_start|>`), Claude, Gemini, and plain text with over 470k–650k ops/sec.
- ⚡ **Zero-Allocation Execution**: High-throughput memory transformations designed for real-time agent loops.

---

## Real-World Use Cases

- 🤖 **Autonomous Coding Agents**: Maintain persistent system instructions while dynamically sliding out old build outputs using token-window trimming.
- 💬 **Infinite Enterprise Chatbots**: Prevent context window overflow by rolling older dialogue turns into background summaries.
- 🎯 **User Profile & Preference Injection**: Dynamically recall user programming styles and platform facts into prompts via semantic memory.
- 🌐 **Multi-Model Provider Swapping**: Switch between Gemini and Claude models on the fly by swapping formatters without changing conversation history.

---

## Memory Patterns Supported

| Pattern Family | Mechanism | Primary Class | Best Use Case |
|:---|:---|:---|:---|
| **Window Memory** | Sliding message, character & token window | `MemoryWindow` | Real-time chat loops, short interactive sessions |
| **Summary Memory** | Rolling LLM-assisted context condensation | `SummaryMemory` | Long-running agent execution, task chains |
| **Semantic Memory** | Relevance & similarity-based recall | `SemanticMemory` | User preferences, long-term memory, knowledge facts |

---

## Performance Benchmarks

Measured on official [JMH Benchmark](examples/Benchmark) (Throughput in `ops/ms`):

```text
Benchmark                                     Mode  Cnt       Score   Units
Benchmark.benchmarkWindowSlidingTrimming     thrpt    3   16410.210  ops/ms
Benchmark.benchmarkSemanticMemoryRecall      thrpt    3     921.450  ops/ms
Benchmark.benchmarkGeminiFormatting          thrpt    3     657.120  ops/ms
Benchmark.benchmarkChatMLFormatting          thrpt    3     470.300  ops/ms
```

> [!NOTE]
> **Environment**: Windows 11, Intel Core i5-1135G7 (Surface Pro 8), JDK 21.0.12. Sliding window trimming executes at over **16.4 million ops/sec**, while prompt formatters deliver over **470,000–657,000 formatting operations/sec**.

---

## API Quick Reference

| Method / Class | Return Type | Description | Docs |
|:---|:---|:---|:---|
| `history.add(role, text)` | `void` | Appends a raw conversation message turn. | [Reference](docs/REFERENCE.md) |
| `history.messages()` | `List<ConversationMessage>` | Returns a thread-safe read-only view of current turns. | [Reference](docs/REFERENCE.md) |
| `MemoryWindow.trimToMessages(list, n)` | `List<ConversationMessage>` | Retains system prompt and latest N messages. | [Reference](docs/REFERENCE.md) |
| `MemoryWindow.trimToEstimatedTokens(list, max)` | `List<ConversationMessage>` | Trims turns to fit under token limits while keeping system prompt. | [Reference](docs/REFERENCE.md) |
| `summaryMem.messages()` | `List<ConversationMessage>` | Returns condensed summary combined with recent turns. | [Reference](docs/REFERENCE.md) |
| `semanticMem.recall(query)` | `List<MemoryEntry>` | Recalls top matching knowledge snippets. | [Reference](docs/REFERENCE.md) |

---

## API Reference

### History & Windows

```java
// Thread-safe Conversation History
ConversationHistory history = new ConversationHistory();
history.system("You are a Java engineer.");
history.user("Explain memory models.");
history.assistant("Java uses JMM...");

// Sliding Window Trimming
List<ConversationMessage> trimmed = MemoryWindow.trimToMessages(history.messages(), 10);
```

### Rolling Summary Memory

```java
// Compacts history when turns exceed threshold
SummaryMemory summaryMem = new SummaryMemory(6, rawDialogue -> {
    return "User is discussing concurrency and zero-allocation pipelines.";
});
summaryMem.user("How do I eliminate allocations?");
```

### Semantic Memory Recall

```java
// Stores and recalls relevant context
SemanticMemory semanticMem = new SemanticMemory(3, null);
semanticMem.remember("arch_goal", "Target 60+ FPS zero GC in timeline orchestration.");
List<SemanticMemory.MemoryEntry> results = semanticMem.recall("FPS timeline");
```

---

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|:---|:---|:---|:---|
| **Memory Orchestration Demo** | [Demo.java](examples/Demo/src/main/java/fastaimemory/Demo.java) | `run-demo.bat` | Interactive CLI demo showcasing Sliding Window, Rolling Summaries, and Semantic Memory. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastaimemory/Benchmark.java) | `run-benchmark.bat` | JMH throughput benchmark for ChatML/Gemini prompt formatting and memory trimming. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastAIMemory - Conversation Memory Engine -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastAIMemory</artifactId>
        <version>0.1.4</version>
    </dependency>

    <!-- FastCore - Required Native Loader -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastAIMemory:0.1.4'
    implementation 'com.github.andrestubbe:FastCore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the release JARs directly from GitHub Releases:

1. 🧠 **[FastAIMemory-0.1.4.jar](https://github.com/andrestubbe/FastAIMemory/releases/tag/0.1.4)** (Memory Orchestrator)
2. ⚙️ **[FastCore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/tag/0.1.0)** (Mandatory Native Loader)

---

## Documentation

- **[REFERENCE.md](docs/REFERENCE.md)**: Core API reference manual and method signatures.
- **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Conversation history condensation and memory patterns.
- **[COMPILE.md](docs/COMPILE.md)**: Build instructions.
- **[CHANGELOG.md](docs/CHANGELOG.md)**: Project history and releases.
- **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones.

---

## Platform Support

| Platform | Architecture | Status | Notes |
|:---|:---:|:---:|:---|
| **Windows 10 / 11** | x64 | ✅ Fully Supported | Pure Java in-process memory management |
| **Linux** | x64 / AArch64 | ✅ Fully Supported | Pure JVM execution across standard architectures |
| **macOS** | Apple Silicon / x64 | ✅ Fully Supported | Pure JVM execution across Apple Silicon & Intel |

---

## Related Projects

- **[`FastAI`](https://github.com/andrestubbe/FastAI)**: Unified AI Client for Java (20+ providers)
- **[`FastAIAgent`](https://github.com/andrestubbe/FastAIAgent)**: Autonomous ReAct Agent Loop and Cognitive Mind
- **[`FastAIBot`](https://github.com/andrestubbe/FastAIBot)**: Zero-Bloat Bot Harnesses and Persona Runtime
- **[`FastAIGraph`](https://github.com/andrestubbe/FastAIGraph)**: In-Memory Knowledge Graph and Multi-Hop Relationship Engine
- **[`FastAIRag`](https://github.com/andrestubbe/FastAIRag)**: In-Process Retrieval-Augmented Generation Substrate
- **[`FastCore`](https://github.com/andrestubbe/FastCore)**: Native Library Loader & JNI Utilities for Java

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.* 🚀