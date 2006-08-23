package be.ordina.unitils.testing.mock.inject;

import be.ordina.unitils.testing.mock.inject.AutoInjector;

import java.util.Map;

/**
 * @author Filip Neven
 */
public class NoInjectionAutoInjector implements AutoInjector {

    public void autoInject(Object object, Map<String, Object> toInject) {
        // This implementation doesn't do autoinjection
    }

}
