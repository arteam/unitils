package org.unitils.objectvalidation.example1;

import java.util.Arrays;


/**
 * A valid dummy bean
 * 
 * @author Matthieu Mestrez
 * @since Oct 17, 2013
 */
public class ValidBeanWithByteArray implements ValidBeanInterface {

    private static final long serialVersionUID = 5304437128249913223L;

    private byte[] firstString;
    
    public ValidBeanWithByteArray() { }

    public ValidBeanWithByteArray(byte[] firstString) {
        this.firstString = firstString;
    }

    public void setFirstString(byte[] firstString) {
        this.firstString = firstString;
    }

    public byte[] getFirstString() {
        return firstString;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(firstString);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValidBeanWithByteArray
            && Arrays.equals(firstString, ((ValidBeanWithByteArray)other).firstString);
    }
    
    @Override
    public String toString() {
        return "ValidBean : [firstString=" + firstString + "]";
    }

}
