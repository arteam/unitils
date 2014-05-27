package org.unitils.objectvalidation.example1;


/**
 * A valid dummy bean
 * 
 * @author Matthieu Mestrez
 * @since Oct 17, 2013
 */
public class ValidBeanOnlyId implements ValidBeanInterface {

    private static final long serialVersionUID = 5304437128249913223L;

    private Long id;
    
    private String firstString;
    
    public ValidBeanOnlyId() { }

    public ValidBeanOnlyId(Long id, String firstString) {
        this.id = id;
        this.firstString = firstString;
    }

    public void setFirstString(String firstString) {
        this.firstString = firstString;
    }

    public String getFirstString() {
        return firstString;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ValidBeanOnlyId other = (ValidBeanOnlyId) obj;
        
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "ValidBean : [id=" + id + ", firstString=" + firstString + "]";
    }

}
