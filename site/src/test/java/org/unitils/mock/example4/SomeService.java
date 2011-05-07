package org.unitils.mock.example4;

// START SNIPPET: prototype
public class SomeService {

    private int maxLength;
    private String defaultValue;

    public SomeService(int maxLength, String defaultValue) {
        this.maxLength = maxLength;
        this.defaultValue = defaultValue;
    }
}
// END SNIPPET: prototype
