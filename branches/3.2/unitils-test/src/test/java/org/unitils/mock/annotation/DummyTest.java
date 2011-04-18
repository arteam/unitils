package org.unitils.mock.annotation;

import junit.framework.Assert;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.dummy.DummyObjectUtilTest;

/**
 * Test the {@link Dummy} and stuffed dummy tag. In depth functionality of these two is tested in {@link DummyObjectUtilTest}
 * 
 * @author Jeroen Horemans
 */
public class DummyTest extends UnitilsJUnit4 {

    @Dummy
    private JustAClass unstuffed;

    @Dummy(stuffed = true)
    private JustAClass stuffed;


    @Test
    public void testStuffed() {
        Assert.assertNotNull(stuffed);
        Assert.assertNotNull(stuffed.getId());
        Assert.assertNotNull(stuffed.getJustAClass());
    }

    @Test
    public void testNormal() {
        Assert.assertNotNull(unstuffed);
        Assert.assertNotNull(unstuffed.getId());
        Assert.assertNull(unstuffed.getJustAClass());
    }


    protected class JustAClass {

        private Long id = 10L;

        private JustAClass justAClass;

        
        public Long getId() {
            return id;
        }

        public JustAClass getJustAClass() {
            return justAClass;
        }

    }
}
