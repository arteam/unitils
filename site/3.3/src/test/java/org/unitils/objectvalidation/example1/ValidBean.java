package org.unitils.objectvalidation.example1;


/**
 * A valid dummy bean
 * 
 * @author Matthieu Mestrez
 * @since Oct 17, 2013
 */
public class ValidBean implements ValidBeanInterface {

    private static final long serialVersionUID = 5304437128249913223L;

    private String firstString;
    
    public ValidBean() { }

    public ValidBean(String firstString) {
        this.firstString = firstString;
    }

    public void setFirstString(String firstString) {
        this.firstString = firstString;
    }

    public String getFirstString() {
        return firstString;
    }

    @Override
    public int hashCode() {
        return firstString == null ? 0 : firstString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ValidBean other = (ValidBean) obj;
        
        if (firstString == null) {
            if (other.firstString != null)
                return false;
        } else if (!firstString.equals(other.firstString))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "ValidBean : [firstString=" + firstString + "]";
    }

}
