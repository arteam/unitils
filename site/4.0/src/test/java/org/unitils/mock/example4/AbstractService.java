package org.unitils.mock.example4;

// START SNIPPET: abstract
public abstract class AbstractService {

    public void doSomething() {
        // ...
        String result = doSomethingAbstract("a value");
        // ...
    }

    protected abstract String doSomethingAbstract(String argument);
}
// END SNIPPET: mailService
