# Java Messaging API

This is a discussion starter for a Java Messaging API. It is fundamentally different from JMS, in that JMS deals with handling one message at a time, where this API deals with streams of messages. By dealing with streams of messages, it makes it very straight forward to plumb different sources and sinks of messages together. Back pressure can also be easily propogated. Complex stream graphs and transformations can be built using high level streaming libraries such as Akka streams or RxJava.

This approach to handling messaging, we feel, is a much better approach to handling messaging in microservices than simply providing an API that handles one message at a time.

The goals/design principles for this initial design are as follows:

* Use reactive streams (soon to be the [JDK9 Flow API](http://hg.openjdk.java.net/jdk9/dev/jdk/file/tip/src/java.base/share/classes/java/util/concurrent/Flow.java)) to handle message publishing and subscribing, in order to facilitate interoperability for the maximum range of messaging sinks and sources with back pressure.
* Allow the messaging implementation to handle all lifecycle and running concerns of streams, including starting the streams, handling errors, backoff on errors and distributing streams across a cluster.

The design has been drawn from our experience in creating similar APIs in Lagom. There are some differences from Lagom, specifically:

* Lagom is a framework that provides symmetric client and server interfaces, that is, the server implements the same interface that the client consumes, or in the context of messaging, the publisher implements the same interface that the subscriber consumes. This symmetry is great for an opinionated framework, but has some major limitations, and hence is probably a non goal for a general purpose messaging abstraction that can be implemented by the a wide range of implementations.
* Lagom's approach to API design, even for the Java APIs, tends to make heavier use of functional programming, and less use of reflection/annotations. This is a style preference for Lagom that probably isn't shared by the majority of potential implementors of a general purpose Java messaging API, and so we have modified Lagom's approach to use annotations in a fashion that would be familiar to someone that has used, for example, JAX-RS.

These departures aside, care has been taken such that the high level approach matches the high level approach that we take in Lagom and underlying technologies, such that this API does benefit from that experience.  Additionally, we are confident that Lagom can provide an implementation of these APIs, and so participate as a compatible implementation.

## In this repository

Perhaps the first thing to read is the [MessageBroker](api/src/main/java/org/example/messaging/MessageBroker.java) javadocs. Then the [MessagePublisher](api/src/main/java/org/example/messaging/MessagePublisher.java) and [MessageSubscriber](api/src/main/java/org/example/messaging/MessageSubscriber.java) annotations.

Finally, there are some examples, one of what it would look like to publish and consume a CQRS event stream in [CQRSExample](examples/src/main/java/org/example/examples/CQRSExample.java), and an example of consuming a message topic and republishing to another topic after doing some transformations in [RepublishExample](examples/src/main/java/org/example/examples/RepublishExample.java).
