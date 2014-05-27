package org.unitils.jodatime;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.jodatime.annotation.FixedDateTime;
import org.unitils.jodatime.annotation.OffsetDateTime;


/**
 * Example test for the {@link JodaTimeModule}.
 * 
 * @author Christophe De Blende
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 1.0.0
 * 
 */
@Ignore
//START SNIPPET: jodatimeExample
@RunWith(UnitilsJUnit4TestClassRunner.class)
@FixedDateTime(datetime = "30/01/2008")
public class DateTimeExample1Test {


    @Test
    public void testTestClassDateTime() {
        // the test class defines the fixed datetime as 30/01/2008, so the current datetime should be equal to that.
        DateTime expected = new DateTime(2008, 1, 30, 0, 0, 0, 0);
        Assert.assertEquals(expected, new DateTime());
    }

    @Test
    @FixedDateTime
    public void testFixedDateTimeDefault() throws InterruptedException {
        DateTime expected = new DateTime(System.currentTimeMillis());
        DateTime actual = new DateTime();

        Assert.assertEquals(expected.getYear(), actual.getYear());
        Assert.assertEquals(expected.getMonthOfYear(), actual.getMonthOfYear());
        Assert.assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
        Assert.assertEquals(expected.getHourOfDay(), actual.getHourOfDay());
        Assert.assertEquals(expected.getMinuteOfDay(), actual.getMinuteOfDay());
        Assert.assertEquals(expected.getSecondOfMinute(), actual.getSecondOfMinute());

    }

    @Test
    @FixedDateTime(datetime = "29/02/2020")
    public void testTestMethodDateTime() {
        // the test method defines the fixed datetime as 29/02/2020, 
        //so the current datetime should be equal to that.
    }

    @Test
    @OffsetDateTime
    public void testCurrentDatetime() throws InterruptedException {
        //OffsetDateTime without value is the current datetime.
    }

    @Test
    @OffsetDateTime(days = 1)
    public void testOffsetDatetime() {
        //the current date should be date from tomorrow.

    }
}
//END SNIPPET: jodatimeExample
