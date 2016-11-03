package org.example.examples;

import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import org.example.messaging.MessageBroker;
import org.example.messaging.MessagePublisher;
import org.example.messaging.MessageSubscriber;
import org.reactivestreams.Processor;

/**
 * This shows an example of republishing messages from one topic to another topic.
 *
 * There are multiple ways to achieve this, the way used in this example achieves at least once semantics.
 */
public class RepublishExample {

    /**
     * Here is an interface with a message publisher.
     *
     * We don't implement this interface, the {@link MessageBroker} will implement it for us to we can publish to the
     * processor it returns.
     */
    interface MyPublisher {
        @MessagePublisher("some-other-topic")
        Processor<SomeOtherEvent, Done> publishSomeOtherEvent();
    }

    /**
     * This subscriber returns a processor that will receive events published to <tt>some-topic</tt>.
     *
     * It then converts these events to another type, and then passes them through the
     * {@link MyPublisher#publishSomeOtherEvent()} processor, which will publish them to <tt>some-other-topic</tt>.
     *
     * It's important to note the use of {@link Processor} here ensures at least once delivery of all messages - the
     * messaging implementation will not consider a message consumed until the {@link #consumeSomeEvent()} processor
     * emits an element, and since it's passing all messages through the {@link MyPublisher#publishSomeOtherEvent()}
     * processor, which won't emit an element until the event is published, this guarantees at least once republishing
     * in this flow.
     */
    @MessageSubscriber("some-topic")
    Processor<SomeEvent, Done> consumeSomeEvent() {
        return Flow.<SomeEvent>create()
                // Convent SomeEvent to SomeOtherEvent
                .map(someEvent -> new SomeOtherEvent(someEvent.getValue()))
                // Send the stream via the publisher
                .via(Flow.fromProcessor(myPublisher::publishSomeOtherEvent))
                // Convert the Akka streams flow to a reactive streams processor.
                        .toProcessor().run(materializer);
    }


    private final MyPublisher myPublisher;
    private final Materializer materializer;

    public RepublishExample(MessageBroker messageBroker, ActorSystem system) {
        this.materializer = ActorMaterializer.create(system);

        // Here we create the publisher, this is a direct client.
        this.myPublisher = messageBroker.create(MyPublisher.class);

        // And we register ourselves as an indirect message handler.
        messageBroker.register(this);
    }

}
