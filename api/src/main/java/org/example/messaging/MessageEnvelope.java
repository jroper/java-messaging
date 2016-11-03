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
     * Create a simple message with no meta data.
     */
    static <Message> MessageEnvelope<Message> create(Message message) {
        return () -> message;
    }

    /**
     * Create a simple message with an offset.
     */
    static <Message> MessageEnvelope<Message> create(Message message, MessageOffset offset) {
        return new MessageEnvelope<Message>() {
            @Override
            public Message message() {
                return message;
            }
            @Override
            public MessageOffset offset() {
                return offset;
            }
        };
    }

}
