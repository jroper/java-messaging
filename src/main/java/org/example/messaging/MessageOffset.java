package org.example.messaging;

import java.util.UUID;

/**
 * A message offset.
 *
 * @author James Roper
 */
public interface MessageOffset {

    /**
     * A sequence message offset, implemented using long's, ordered by the natural ordering of the longs.
     */
    final class Sequence implements MessageOffset {
        private final long value;

        public Sequence(long value) {
            this.value = value;
        }

        long value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Sequence sequence = (Sequence) o;

            return value == sequence.value;

        }

        @Override
        public int hashCode() {
            return (int) (value ^ (value >>> 32));
        }

        @Override
        public String toString() {
            return "Sequence{" +
                    "value=" + value +
                    '}';
        }
    }

    /**
     * A time based UUID message offset, ordered by the timestamps in the UUIDs.
     */
    final class TimeUuid implements MessageOffset {
        private final UUID value;

        public TimeUuid(UUID value) {
            this.value = value;
        }

        public UUID value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TimeUuid timeUuid = (TimeUuid) o;

            return value.equals(timeUuid.value);

        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return "TimeUuid{" +
                    "value=" + value +
                    '}';
        }
    }

    /**
     * No message offset, possibly indicating that no message has ever been processed, or that this stream does not
     * use message offsets.
     */
    enum None implements MessageOffset {
        INSTANCE;
    }


}
