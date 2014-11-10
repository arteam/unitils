package org.unitils.jbehave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Test if everything from the {@link org.unitils.mail.MailModule} works.
 * 
 * @author Willemijn Wouters
 * 
 * @since 1.0.0
 * 
 */
//START SNIPPET: jbehavetest
public class SimpleMailTest extends UnitilsJUnitStories {


    /**
     * @see org.unitils.jbehave.UnitilsJunitStories#getSteps()
     */
    @Override
    public List<Object> getSteps() {
        List<Object> lst = new ArrayList<Object>();
        lst.add(new SimpleMailStep());
        return lst;
    }

    /**
     * @see org.jbehave.core.junit.JUnitStories#storyPaths()
     */
    @Override
    protected List<String> storyPaths() {
        return Arrays.asList("org/unitils/jbehave/stories/Mail.story");
    }

}
//END SNIPPET: jbehavetest