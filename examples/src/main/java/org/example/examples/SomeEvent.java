package org.example.examples;

/**
 * An example event.
 */
public class SomeEvent {
    private final String value;

    public SomeEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
