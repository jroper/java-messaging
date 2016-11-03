# Java Messaging API

This is a discussion starter for a Java Messaging API.

The goals/design principles for this initial design are as follows:

* Use reactive streams (soon to be the [JDK9 Flow API](http://hg.openjdk.java.net/jdk9/dev/jdk/file/tip/src/java.base/share/classes/java/util/concurrent/Flow.java)) to handle message publishing and subscribing, in order to facilitate interoperability for the maximum range of messaging sinks and sources with back pressure.
* Allow the messaging implementation to handle all lifecycle and running concerns of streams, including starting the streams, handling errors, backoff on errors and distributing streams across a cluster.

The design has been drawn from our experience in creating similar APIs in Lagom. There are some differences from Lagom, specifically:

* Lagom is a framework that provides symmetric client and server interfaces, that is, the server implements the same interface that the client consumes, or in the context of messaging, the publisher implements the same interface that the subscriber consumes. This symmetry is great for an opinionated framework, but has some major limitations, and hence is probably a non goal for a general purpose messaging abstraction that can be implemented by the a wide range of implementations.
* Lagom's approach to API design, even for the Java APIs, tends to make heavier use of functional programming, and less use of reflection/annotations. This is a style preference for Lagom that probably isn't shared by the majority of potential implementors of a general purpose Java messaging API, and so we have modified Lagom's approach to use annotations in a fashion that would be familiar to someone that has used, for example, JAX-RS.

These departures aside, care has been taken such that the high level approach matches the high level approach that we take in Lagom and underlying technologies, such that this API does benefit from that experience.  Additionally, we are confident that Lagom can provide an implementation of these APIs, and so participate as a compatible implementation.
