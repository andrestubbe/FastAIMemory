# FastAIMemory API Reference

## Core Classes

### `ConversationHistory`
Thread-safe conversation manager supporting role appending (`system`, `user`, `assistant`) and format conversions (`ChatML`, `Gemini`, `Raw`).

### `MemoryWindow`
Utility for sliding context window trimming:
* `trimToMessages(List<ConversationMessage> messages, int maxMessages)`: Trims older messages while preserving system prompts.
* `trimToEstimatedTokens(List<ConversationMessage> messages, int maxTokens)`: Trims context to a target token budget.

### `SummaryMemory`
Rolling LLM-assisted background summary memory for long-running agent threads.

### `SemanticMemory`
Dynamic memory buffer with keyword and similarity recall.
