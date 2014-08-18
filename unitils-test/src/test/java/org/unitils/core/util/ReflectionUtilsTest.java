package org.unitils.core.util;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.util.ReflectionUtils;


/**
 * Test <a href="https://jira.smals.be/i#browse/STVE-183">STVE-183</a>.
 * 
 * @author Willemijn Wouters
 * 
 * @since 
 * 
 */
public class ReflectionUtilsTest {

    @Test
    public void testString() throws NoSuchMethodException, SecurityException {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "str", false));
        Method setter = TestClass1.class.getMethod("setStr", String.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "str", false));
    }
    
    @Test
    public void testIntegerPrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "i", false));
        Method setter = TestClass1.class.getMethod("setI", Integer.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "i", false));
    }
    
    @Test
    public void testIntegerWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperI", false));
        Method setter = TestClass1.class.getMethod("setWrapperI", Integer.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperI", false));
    }
    
    @Test
    public void testDoublePrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "d", false));
        Method setter = TestClass1.class.getMethod("setD", Double.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "d", false));
    }
    @Test
    public void testDoubleWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperD", false));
        Method setter = TestClass1.class.getMethod("setWrapperD", Double.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperD", false));
    }
    
    @Test
    public void testBooleanPrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "b", false));
        Method setter = TestClass1.class.getMethod("setB", Boolean.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "b", false));
    }
    
    @Test
    public void testBooleanWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperB", false));
        Method setter = TestClass1.class.getMethod("setWrapperB", Boolean.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperB", false));
    }
    
    @Test
    public void testShortPrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "s", false));
        Method setter = TestClass1.class.getMethod("setS", Short.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "s", false));
    }
    
    @Test
    public void testShortWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperS", false));
        Method setter = TestClass1.class.getMethod("setWrapperS", Short.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperS", false));
    }
    
    @Test
    public void testLongPrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "l", false));
        Method setter = TestClass1.class.getMethod("setL", Long.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "l", false));
    }
    
    @Test
    public void testLongWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperL", false));
        Method setter = TestClass1.class.getMethod("setWrapperL", Long.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperL", false));
    }
    
    @Test
    public void testFloatPrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "f", false));
        Method setter = TestClass1.class.getMethod("setF", Float.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "f", false));
    }
    
    @Test
    public void testFloatWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperF", false));
        Method setter = TestClass1.class.getMethod("setWrapperF", Float.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperF", false));
    }
    
    @Test
    public void testCharPrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "c", false));
        Method setter = TestClass1.class.getMethod("setC", Character.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "c", false));
    }
    
    @Test
    public void testCharWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperC", false));
        Method setter = TestClass1.class.getMethod("setWrapperC", Character.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperC", false));
    }
    
    @Test
    public void testBytePrimitive() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "byteVar", false));
        Method setter = TestClass1.class.getMethod("setByteVar", Byte.TYPE);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "byteVar", false));
    }
    
    @Test
    public void testByteWrapper() throws Exception {
        Assert.assertNotNull(ReflectionUtils.getGetter(TestClass1.class, "wrapperByte", false));
        Method setter = TestClass1.class.getMethod("setWrapperByte", Byte.class);
        Assert.assertNotNull(ReflectionUtils.getGetter(setter, false));
        Assert.assertNotNull(ReflectionUtils.getSetter(TestClass1.class, "wrapperByte", false));
    }
    
    
    /***/
    @SuppressWarnings("unused")
    private class TestClass1 {
        
        private String str;
        
        private int i;
        
        private Integer wrapperI;
        
        private double d;
        
        private Double wrapperD;
        
        private boolean b;
        
        private Boolean wrapperB;
        
        private byte byteVar;
        
        private Byte wrapperByte;
        
        private short s;
        
        private Short wrapperS;
        
        private long l;
        
        private Long wrapperL;
        
        private float f;
        
        private Float wrapperF;
        
        private char c;
        
        private Character wrapperC;

        
        /**
         * @return the str
         */
        public String getStr() {
            return str;
        }

        
        /**
         * @param str the str to set
         */
        public void setStr(String str) {
            this.str = str;
        }

        
        /**
         * @return the i
         */
        public int getI() {
            return i;
        }

        
        /**
         * @param i the i to set
         */
        public void setI(int i) {
            this.i = i;
        }

        
        /**
         * @return the wrapperI
         */
        public Integer getWrapperI() {
            return wrapperI;
        }

        
        /**
         * @param wrapperI the wrapperI to set
         */
        public void setWrapperI(Integer wrapperI) {
            this.wrapperI = wrapperI;
        }

        
        /**
         * @return the d
         */
        public double getD() {
            return d;
        }

        
        /**
         * @param d the d to set
         */
        public void setD(double d) {
            this.d = d;
        }

        
        /**
         * @return the wrapperD
         */
        public Double getWrapperD() {
            return wrapperD;
        }

        
        /**
         * @param wrapperD the wrapperD to set
         */
        public void setWrapperD(Double wrapperD) {
            this.wrapperD = wrapperD;
        }

        
        /**
         * @return the b
         */
        public boolean isB() {
            return b;
        }

        
        /**
         * @param b the b to set
         */
        public void setB(boolean b) {
            this.b = b;
        }

        
        /**
         * @return the wrapperB
         */
        public Boolean getWrapperB() {
            return wrapperB;
        }

        
        /**
         * @param wrapperB the wrapperB to set
         */
        public void setWrapperB(Boolean wrapperB) {
            this.wrapperB = wrapperB;
        }

        
        /**
         * @return the byteVar
         */
        public byte getByteVar() {
            return byteVar;
        }

        
        /**
         * @param byteVar the byteVar to set
         */
        public void setByteVar(byte byteVar) {
            this.byteVar = byteVar;
        }

        
        /**
         * @return the wrapperByte
         */
        public Byte getWrapperByte() {
            return wrapperByte;
        }

        
        /**
         * @param wrapperByte the wrapperByte to set
         */
        public void setWrapperByte(Byte wrapperByte) {
            this.wrapperByte = wrapperByte;
        }

        
        /**
         * @return the s
         */
        public short getS() {
            return s;
        }

        
        /**
         * @param s the s to set
         */
        public void setS(short s) {
            this.s = s;
        }

        
        /**
         * @return the wrapperS
         */
        public Short getWrapperS() {
            return wrapperS;
        }

        
        /**
         * @param wrapperS the wrapperS to set
         */
        public void setWrapperS(Short wrapperS) {
            this.wrapperS = wrapperS;
        }

        
        /**
         * @return the l
         */
        public long getL() {
            return l;
        }

        
        /**
         * @param l the l to set
         */
        public void setL(long l) {
            this.l = l;
        }

        
        /**
         * @return the wrapperL
         */
        public Long getWrapperL() {
            return wrapperL;
        }

        
        /**
         * @param wrapperL the wrapperL to set
         */
        public void setWrapperL(Long wrapperL) {
            this.wrapperL = wrapperL;
        }

        
        /**
         * @return the f
         */
        public float getF() {
            return f;
        }

        
        /**
         * @param f the f to set
         */
        public void setF(float f) {
            this.f = f;
        }

        
        /**
         * @return the wrapperF
         */
        public Float getWrapperF() {
            return wrapperF;
        }

        
        /**
         * @param wrapperF the wrapperF to set
         */
        public void setWrapperF(Float wrapperF) {
            this.wrapperF = wrapperF;
        }

        
        /**
         * @return the c
         */
        public char getC() {
            return c;
        }

        
        /**
         * @param c the c to set
         */
        public void setC(char c) {
            this.c = c;
        }

        
        /**
         * @return the wrapperC
         */
        public Character getWrapperC() {
            return wrapperC;
        }

        
        /**
         * @param wrapperC the wrapperC to set
         */
        public void setWrapperC(Character wrapperC) {
            this.wrapperC = wrapperC;
        }
        
        
        
    }

}
