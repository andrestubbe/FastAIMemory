package fastaimemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Semantic Memory Pattern — Retrieves relevant context and past memory snippets
 * based on embedding similarity or keyword relevance to the user prompt.
 */
public final class SemanticMemory {

    /**
     * Memory entry containing semantic content and relevance tags.
     */
    public record MemoryEntry(String id, String content, long timestamp) {
    }

    private final List<MemoryEntry> store = new ArrayList<>();
    private final BiFunction<String, List<MemoryEntry>, List<MemoryEntry>> retrievalFunction;
    private final int topK;

    public SemanticMemory(final int topK, final BiFunction<String, List<MemoryEntry>, List<MemoryEntry>> retrievalFunction) {
        this.topK = topK > 0 ? topK : 3;
        this.retrievalFunction = retrievalFunction != null ? retrievalFunction : this::defaultSimpleRetrieval;
    }

    public synchronized void remember(final String id, final String content) {
        this.store.add(new MemoryEntry(id, content, System.currentTimeMillis()));
    }

    public synchronized void remember(final String content) {
        this.remember(String.valueOf(System.currentTimeMillis()), content);
    }

    public synchronized List<MemoryEntry> recall(final String query) {
        if (query == null || query.isEmpty() || this.store.isEmpty()) {
            return Collections.emptyList();
        }
        return this.retrievalFunction.apply(query, new ArrayList<>(this.store));
    }

    public synchronized List<MemoryEntry> all() {
        return Collections.unmodifiableList(new ArrayList<>(this.store));
    }

    public synchronized void clear() {
        this.store.clear();
    }

    private List<MemoryEntry> defaultSimpleRetrieval(final String query, final List<MemoryEntry> entries) {
        final String[] terms = query.toLowerCase().split("\\s+");
        final List<MemoryEntry> matches = new ArrayList<>();

        for (final MemoryEntry entry : entries) {
            final String lowerContent = entry.content().toLowerCase();
            for (final String term : terms) {
                if (term.length() > 2 && lowerContent.contains(term)) {
                    matches.add(entry);
                    break;
                }
            }
            if (matches.size() >= this.topK) {
                break;
            }
        }
        return matches;
    }
}
