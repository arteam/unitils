package org.unitils.spring.profile;


/**
 * How do you want to configure you're beans? Do you want to use the annotation {@link Configuration} or the annotation {@link SpringApplicationContext}.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public enum TypeConfiguration {
    /** 
     * You can use the annotation {@link Configuration} to define the beans in the applicationcontext.
     * The beans are defined in a seperate class and contains the annotation {@link Configuration}
     * example: 
     * <code>
     * @Configuration <br/>
     * @Profile("dev")<br/>
     * public class StandaloneDataConfig {
     * </code>
     */
    CONFIGURATION,
    /** 
     * You can use the annotation {@link SpringApplicationContext} to define the beans in de applicationcontext.
     * All the beans are defined in an xml file. <br/>
     * {@code
     * <beans ... profile="..."> ... </beans>
     * }
     */
    APPLICATIONCONTEXT
}
