package fastaimemory;

import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JMH Microbenchmark — FastAIMemory formatting, sliding window trimming, and memory compaction.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-server", "-XX:+UseG1GC", "-Xms256m", "-Xmx256m"})
public class FastAIMemoryBenchmark {

    private ConversationHistory history;
    private MemoryContextBuilder chatMLBuilder;
    private MemoryContextBuilder geminiBuilder;

    @Setup(Level.Trial)
    public void setup() {
        this.history = new ConversationHistory();
        this.history.system("You are an ultra-fast Java engineer.");
        for (int i = 0; i < 20; i++) {
            this.history.user("User message payload " + i + " with some detailed instruction text.");
            this.history.assistant("Assistant response payload " + i + " containing simulated output.");
        }

        this.chatMLBuilder = new MemoryContextBuilder(new ChatMLFormatter());
        this.geminiBuilder = new MemoryContextBuilder(new GeminiFormatter());
    }

    @Benchmark
    public String benchmarkChatMLFormatting() {
        return this.chatMLBuilder.build(this.history);
    }

    @Benchmark
    public String benchmarkGeminiFormatting() {
        return this.geminiBuilder.build(this.history);
    }

    @Benchmark
    public List<ConversationMessage> benchmarkWindowSlidingTrimming() {
        return MemoryWindow.trimToMessages(this.history.messages(), 10);
    }

    @Benchmark
    public List<SemanticMemory.MemoryEntry> benchmarkSemanticMemoryRecall() {
        final SemanticMemory memory = new SemanticMemory(2, null);
        memory.remember("k1", "User prefers Java 17+, zero-dependency libraries and strict performance.");
        memory.remember("k2", "User enforces strict final and this keyword strategies.");
        return memory.recall("Show me Java guidelines");
    }
}
