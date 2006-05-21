/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.mock;

import junit.framework.TestCase;
import org.easymock.classextension.internal.ClassExtensionHelper;

import java.util.HashSet;
import java.util.Set;

import static org.easymock.classextension.EasyMock.*;

/**
 * @author Filip Neven
 */
public class EasyMockTestCase extends TestCase {

    private Set mocks = new HashSet();

    protected <T> T getMock(Class<T> clazz) {
        T result = createMock(clazz);
        registerMock(result);
        return result;
    }

    private void registerMock(Object mock) {
        mocks.add(mock);
    }

    protected void replay() {
        for (Object mock : mocks) {
            ClassExtensionHelper.getControl(mock).replay();
        }
    }

    protected void verify() {
        for (Object mock : mocks) {
            ClassExtensionHelper.getControl(mock).verify();
        }
    }

}
