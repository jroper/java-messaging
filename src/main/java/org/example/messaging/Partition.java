package org.example.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates to the framework that the parameter with this annotation should be passed the partition number
 *
 * @see Partitioned
 * @author James Roper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Partition {
}
