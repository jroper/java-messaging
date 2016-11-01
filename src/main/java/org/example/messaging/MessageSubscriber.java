package org.example.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Message subscriber.
 *
 * A message subscriber is a method that returns either a {@link org.reactivestreams.Subscriber} or
 * {@link org.reactivestreams.Processor} from an indirect message handler, or returns a
 * {@link org.reactivestreams.Publisher} from a direct message broker client.
 *
 * If the stream returned is a {@link org.reactivestreams.Processor}, each element emitted by the processor will be an
 * acknowledgement of an element consumed by the processor has been successfully consumed.
 *
 * The type of elements that the <tt>Subscriber</tt> or <tt>Processor</tt> can either be the message itself, or can be
 * a {@link MessageEnvelope}, which allows access to message meta data as well as the ability to manually acknowledge
 * the message.
 *
 * @author James Roper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageSubscriber {
    // The name of the topic this subscriber subscribes to.
    String value();
}
