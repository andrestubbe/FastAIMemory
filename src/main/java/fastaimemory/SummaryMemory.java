package fastaimemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Summary Memory Pattern — Condenses aging conversation history into a rolling summary
 * while retaining recent interactive dialogue turns and core system instructions.
 */
public final class SummaryMemory {

    private final ConversationHistory history;
    private final int maxRecentMessages;
    private String currentSummary;
    private Function<String, String> summarizerFunction;

    public SummaryMemory(final int maxRecentMessages) {
        this(maxRecentMessages, null);
    }

    public SummaryMemory(final int maxRecentMessages, final Function<String, String> summarizerFunction) {
        this.history = new ConversationHistory();
        this.maxRecentMessages = maxRecentMessages > 0 ? maxRecentMessages : 6;
        this.summarizerFunction = summarizerFunction;
        this.currentSummary = null;
    }

    public synchronized void user(final String text) {
        this.history.user(text);
        this.compactIfNeeded();
    }

    public synchronized void assistant(final String text) {
        this.history.assistant(text);
        this.compactIfNeeded();
    }

    public synchronized void system(final String text) {
        this.history.system(text);
    }

    public synchronized String summary() {
        return this.currentSummary;
    }

    public synchronized List<ConversationMessage> messages() {
        final List<ConversationMessage> raw = this.history.messages();
        if (this.currentSummary == null || this.currentSummary.isEmpty()) {
            return raw;
        }

        final List<ConversationMessage> result = new ArrayList<>();
        boolean systemInserted = false;

        for (final ConversationMessage msg : raw) {
            if (msg.role() == ConversationRole.SYSTEM) {
                result.add(new ConversationMessage(ConversationRole.SYSTEM, msg.text() + "\n\n[Summary of previous context]:\n" + this.currentSummary));
                systemInserted = true;
            } else {
                result.add(msg);
            }
        }

        if (!systemInserted) {
            result.add(0, new ConversationMessage(ConversationRole.SYSTEM, "[Summary of previous context]:\n" + this.currentSummary));
        }

        return Collections.unmodifiableList(result);
    }

    private void compactIfNeeded() {
        final List<ConversationMessage> all = this.history.messages();
        // Count non-system messages
        int nonSystemCount = 0;
        for (final ConversationMessage msg : all) {
            if (msg.role() != ConversationRole.SYSTEM) {
                nonSystemCount++;
            }
        }

        // Trigger compaction when exceeding 2x recent threshold
        if (nonSystemCount > this.maxRecentMessages * 2 && this.summarizerFunction != null) {
            final StringBuilder toSummarize = new StringBuilder();
            if (this.currentSummary != null && !this.currentSummary.isEmpty()) {
                toSummarize.append("Existing Summary: ").append(this.currentSummary).append("\n\n");
            }
            toSummarize.append("New Dialogue to condense:\n");

            final int keepRecentIndex = all.size() - this.maxRecentMessages;
            final List<ConversationMessage> retained = new ArrayList<>();

            for (int i = 0; i < all.size(); i++) {
                final ConversationMessage msg = all.get(i);
                if (msg.role() == ConversationRole.SYSTEM) {
                    retained.add(msg);
                } else if (i < keepRecentIndex) {
                    toSummarize.append(msg.role().name()).append(": ").append(msg.text()).append("\n");
                } else {
                    retained.add(msg);
                }
            }

            try {
                this.currentSummary = this.summarizerFunction.apply(toSummarize.toString());
                this.history.clear();
                for (final ConversationMessage m : retained) {
                    this.history.add(m.role(), m.text());
                }
            } catch (final Exception ignored) {
                // Keep history intact if summarization fails
            }
        }
    }

    public void setSummarizer(final Function<String, String> summarizerFunction) {
        this.summarizerFunction = summarizerFunction;
    }
}
