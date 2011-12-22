package org.unitils.mock.example1;

// START SNIPPET: partial
public class SomeService {

    public int method1() {
        int result1 = method2();
        int result2 = method3();
        return result1 + result2;
    }

    protected int method2() {
        return 1000;
    }

    protected int method3() {
        return 2000;
    }
}
// END SNIPPET: partial
