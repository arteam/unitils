package org.unitils;

import org.unitils.dbunit.annotation.DataSet;


/**
 * Just A Testclass.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@DataSet
public class JustATestClass {
    private Integer i;
    private String j;
    private Double k;
    
    

    /**
     * @param i
     * @param j
     * @param k
     */
    public JustATestClass(Integer i, String j, Double k) {
        super();
        this.i = i;
        this.j = j;
        this.k = k;
    }



    public void method1() {
        //do nothing
    }
    
    
    
    /**
     * @return the i
     */
    public Integer getI() {
        return i;
    }
    
    
    /**
     * @return the j
     */
    public String getJ() {
        return j;
    }
    
    
    /**
     * @return the k
     */
    public Double getK() {
        return k;
    }
}
