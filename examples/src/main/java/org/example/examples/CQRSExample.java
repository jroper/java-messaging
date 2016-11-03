package org.example.examples;

import akka.Done;
import akka.actor.ActorSystem;
import akka.persistence.query.*;
import akka.persistence.query.javadsl.EventsByTagQuery2;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import org.example.messaging.*;
import org.example.messaging.Partition;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * An example that shows some typical CQRS message publishing and consuming use cases.
 */
public class CQRSExample {

    /**
     * This shows a message publisher that publishes messages from a persistent store, in this case it's from Akka
     * persistence query. This would be a typical CQRS use case, you have some event sourcing library (in this case
     * Akka persistence) that is able to produce streams of events, and you want to publish those events to a message
     * bus.
     *
     * In this example, the streams are partitioned in the event storage, we have 20 partitions, and the message
     * publisher will effectively invoke publishSomeEvent 20 times, once for each partition, and assuming this publisher
     * is distributed across many nodes in a cluster, this will allow the publishing load to be distributed across the
     * cluster. In this specific example, unless throughput is incredibly high, this is probably not necessary since
     * events from the persistent store are being published as is. However, if some transformations were being done to
     * the events, particularly if those transformations perhaps required looking up further data from a database or
     * another service, before publishing them, then this partitioning may be necessary to achieve moderate throughputs.
     */
    @MessagePublisher("some-topic")
    @Partitioned(20)
    public Publisher<MessageEnvelope<SomeEvent>> publishSomeEvent(
            MessageOffset offset,
            @Partition int partition
    ) {
        // First load the source of events from Akka persistence query
        return someEventsByTagQuery.eventsByTag("sometag-" + partition, convertOffset(offset))
                // Now convert the events to messaging MessageEnvelope, this allows us to pass the offset back
                // to the messaging implementation, which will use that to resume the stream if it gets interrupted by
                // passing it to the MessageOffset parameter above.
                .map(event -> MessageEnvelope.create((SomeEvent) event.event(), convertAkkaOffset(event.offset())))
                // And convert the Akka streams source to a reactive streams publisher
                .runWith(Sink.asPublisher(AsPublisher.WITHOUT_FANOUT), materializer);
    }

    /**
     * This shows a message subscriber that consumes messages published by the above message publisher.
     *
     * This would be a typical CQRS read side use case.
     */
    @MessageSubscriber("some-topic")
    public Processor<SomeEvent, Done> consumeSomeEvent() {
        return Flow.<SomeEvent>create().map(event -> {

            // Do something like update a read side database table, or just log to standard out.
            System.out.println("I got an event: " + event);
            return Done.getInstance();

        }).toProcessor().run(materializer);
    }

    /**
     * Same as above, but with at most once semantics, since there's no way to acknowledge that each message was
     * successfully handled.
     */
    @MessageSubscriber("some-topic")
    public Subscriber<SomeEvent> consumeSomeEventAtMostOnce() {
        // Create a source that will materialize to a subscriber
        return Source.<SomeEvent>asSubscriber()
                .toMat(Sink.foreach(event -> {

                    // Do something like update a read side database table, or just log to standard out.
                    System.out.println("I got an event: " + event);

                }), Keep.left())
                .run(materializer);
    }

    /**
     * Same as above, but with at least once semantics via explicit commit.
     */
    @MessageSubscriber("some-topic")
    public Subscriber<MessageEnvelope<SomeEvent>> consumeSomeEventExplicitCommit() {
        // Create a source that will materialize to a subscriber
        return Source.<MessageEnvelope<SomeEvent>>asSubscriber()
                .toMat(Sink.foreach(event -> {

                    // Do something like update a read side database table, or just log to standard out.
                    System.out.println("I got an event: " + event.message());

                    // Commit the event to acknowledge that we handled it.
                    event.commit();

                }), Keep.left())
                .run(materializer);
    }

    // Mechanics below are not strictly relevant to the message publisher API.
    private final EventsByTagQuery2 someEventsByTagQuery;
    private final Materializer materializer;

    // Dependencies for using Akka persistence query.
    public CQRSExample(MessageBroker messageBroker, ActorSystem system) {
        this.materializer = ActorMaterializer.create(system);
        this.someEventsByTagQuery = PersistenceQuery.get(system).getReadJournalFor(LeveldbReadJournal.class,
                LeveldbReadJournal.Identifier());

        // This is where we register ourselves to the message broker as a message handler.
        messageBroker.register(this);
    }

    /**
     * Convert a messaging offset to an Akka persistence query offset.
     */
    private Offset convertOffset(MessageOffset offset) {
        if (offset instanceof MessageOffset.Sequence) {
            return new Sequence(((MessageOffset.Sequence) offset).value());
        } else if (offset instanceof MessageOffset.TimeUuid) {
            return new TimeBasedUUID(((MessageOffset.TimeUuid) offset).value());
        } else if (offset.equals(MessageOffset.None.INSTANCE)) {
            return NoOffset$.MODULE$;
        } else {
            throw new IllegalArgumentException("Unknown offset type: " + offset);
        }
    }

    /**
     * Convert an Akka persistence query offset to a messaging offset.
     */
    private MessageOffset convertAkkaOffset(Offset offset) {
        if (offset instanceof Sequence) {
            return new MessageOffset.Sequence(((Sequence) offset).value());
        } else if (offset instanceof TimeBasedUUID) {
            return new MessageOffset.TimeUuid(((TimeBasedUUID) offset).value());
        } else if (offset.equals(NoOffset$.MODULE$)) {
            return MessageOffset.None.INSTANCE;
        } else {
            throw new IllegalArgumentException("Unknown offset type: " + offset);
        }
    }
}