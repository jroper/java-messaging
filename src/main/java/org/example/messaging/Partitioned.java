package org.example.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates to the framework that a {@link MessagePublisher} source is partitioned.
 *
 * The framework will distributed these message publishers across a cluster of nodes for the one service, allowing the
 * publishing load for the message publisher to be shared by many nodes.
 *
 * This should be used in combination with the {@link Partition} method, so that the framework can indicate which
 * partition it getting the publisher for.
 *
 * @author James Roper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Partitioned {

    /**
     * The number of partitions.
     *
     * If not set, relies on some external configuration for the number of partitions.
     */
    int value() default -1;
}
