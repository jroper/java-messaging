package org.example.messaging;

/**
 * A message envelope.
 *
 * @author James Roper
 */
public interface MessageEnvelope<Message> {

    // Methods to provide various metadata, potentially implementation specific, can be provided here.

    /**
     * Get the message.
     */
    Message message();

    /**
     * Get the offset for this message.
     */
    default MessageOffset offset() {
        return MessageOffset.None.INSTANCE;
    }

    /**
     * Commit the message, that is, acknowledge it was successfully processed.
     */
    default void commit() {
    }

    /**
     * Create a message.
     */
    static <Message> MessageEnvelope<Message> create(Message message) {
        return () -> message;
    }
}
