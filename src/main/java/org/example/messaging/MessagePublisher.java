package org.example.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Message publisher.
 *
 * A message publisher is a method that returns a {@link org.reactivestreams.Publisher} on an indirect message handler,
 * or returns a {@link org.reactivestreams.Subscriber} or {@link org.reactivestreams.Processor} from a direct message
 * broker client.
 *
 * If the stream returned is a {@link org.reactivestreams.Processor}, each element emitted by the processor will be an
 * acknowledgement of an element consumed by the processor having been put on the messaging queue.
 *
 * The type of elements that the streams emit or consume can either be the message itself, or can be a
 * {@link MessageEnvelope}, which can be used to convey meta data, and can also allow the framework to invoke
 * {@link MessageEnvelope#commit()} to acknowledge that the message has been sent.
 *
 * Indirect message publisher methods may also accept a {@link MessageOffset} parameter, allowing them to resume
 * publishing from the last {@link MessageOffset} that was successfully published.
 *
 * @author James Roper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessagePublisher {
    /**
     * The name of the topic that this publisher publishes to.
     */
    String value();
}
