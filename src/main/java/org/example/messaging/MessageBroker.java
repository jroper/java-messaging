package org.example.messaging;

/**
 * Abstraction over messaging.
 *
 * There are two ways to interact with the message broker, indirectly and directly.
 *
 * When interacting indirectly, the {@link #register(Object)} method is used. In this case, the message broker
 * implementation will inspect the methods on the passed in object, find all the {@link MessagePublisher} and
 * {@link MessageSubscriber} methods, and invoke them accordingly, wiring them up to the underlying message broker
 * streams.
 *
 * In this case, subscriptions, stream lifecycles, errors, partitioning and sharding, backoff and reconnections are all
 * handled by the implementation, and are not an end user concern. The method is said to be indirect because the handler
 * does not directly work with the streams, it just returns the publishers and subscribers, and lets the subscriber
 * work with them.
 *
 * The direct method is used by invoking the {@link #create(Class)}. This will create a message broker client, returning
 * a dynamic implementation of the passed in interface. Invoking these methods allows the caller to get a hold of the
 * publishers and subscribers, allowing them to directly work with them.
 *
 * In this case, it's the callers responsibility to deal with subscriptions, stream lifecycles, errors, partitioning
 * and sharding, backoff and reconnections, or perhaps to return the streams from invocations on an indirect handler to
 * let them be managed by the message broker implementation.
 *
 * @author James Roper
 */
public interface MessageBroker {

    /**
     * Register a message broker handler.
     *
     * A message broker handler is an object with one or more {@link MessagePublisher} or {@link MessageSubscriber}
     * methods. The messaging framework will invoke these methods to obtain publishers and subscribers to publish and
     * consume messages from message broker topics.
     *
     * Using this method for handling streams is recommended over using {@link #create(Class)}, since it lets the
     * framework manage stream lifecycles, errors, partitioning and sharding, backoff, reconnections etc for you.
     *
     * @param handler The messaging handler.
     */
    void register(Object handler);

    /**
     * Create a message client.
     *
     * A client interface is one that has one or more {@link MessagePublisher} or {@link MessageSubscriber} annotated
     * methods. This method will return an implementation of the interface that allows consuming and or publishing
     * messages on demand.
     *
     * It is recommended that you use {@link #register(Object)} over this method as you will have to manually manage
     * stream lifecycles, errors, partitioning and sharding, backoff, reconnections etc for the streams that the client
     * returns.
     *
     * Where this is primarily useful is in chaining message topics together, such as where a handler passed to
     * {@link #register(Object)} has a message publisher that consumes a message subsbcriber, in that case the stream
     * lifecycle is already being managed by the framework.
     *
     * @param clientInterface An interface that contains one or more message publisher or message subscriber methods.
     * @return Returns an implementation of that interface that allows the streams to be consumed directly.
     */
    <T> T create(Class<? extends T> clientInterface);
}
