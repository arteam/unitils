package org.unitils.reflectionassert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.*;
import static org.unitils.reflectionassert.ReflectionComparatorMode.*;

public class ReflectionAssertExamples {

    @Test(expected = AssertionError.class)
    public void noEquals() {
        // START SNIPPET: noEquals
        User user1 = new User(1, "John", "Doe");
        User user2 = new User(1, "John", "Doe");
        assertEquals(user1, user2);
        // END SNIPPET: noEquals
    }

    @Test
    public void usingEquals() {
        // START SNIPPET: usingEquals
        UserWithEquals user1 = new UserWithEquals(1, "John", "Doe");
        UserWithEquals user2 = new UserWithEquals(1, "Jane", "Smith");
        assertEquals(user1, user2);
        // END SNIPPET: usingEquals
    }

    @Test
    public void allFields() {
        // START SNIPPET: allFields
        User user1 = new User(1, "John", "Doe");
        User user2 = new User(1, "John", "Doe");
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getFirst(), user2.getFirst());
        assertEquals(user1.getLast(), user2.getLast());
        // END SNIPPET: allFields
    }

    @Test
    public void reflection() {
        // START SNIPPET: reflection
        User user1 = new User(1, "John", "Doe");
        User user2 = new User(1, "John", "Doe");
        assertReflectionEquals(user1, user2);
        // END SNIPPET: reflection
    }

    @Test
    public void examples() {
        // START SNIPPET: examples
        assertReflectionEquals(1, 1L);

        List<Double> myList = new ArrayList<Double>();
        myList.add(1.0);
        myList.add(2.0);
        assertReflectionEquals(asList(1, 2), myList);
        // END SNIPPET: examples
    }

    @Test
    public void lenientOrder() {
        // START SNIPPET: lenientOrder
        List<Integer> myList = asList(3, 2, 1);
        assertReflectionEquals(asList(1, 2, 3), myList, LENIENT_ORDER);
        // END SNIPPET: lenientOrder
    }

    @Test
    public void ignoreDefaults() {
        // START SNIPPET: ignoreDefaults
        User actualUser = new User(1, "John", "Doe", new Address("First street", 12, "Brussels"));
        User expectedUser = new User(1, "John", null, new Address("First street", null, null));
        assertReflectionEquals(expectedUser, actualUser, IGNORE_DEFAULTS);
        // END SNIPPET: ignoreDefaults
    }

    @Test(expected = AssertionError.class)
    public void ignoreDefaultsSide() {
        Integer anyObject = 5;
        // START SNIPPET: ignoreDefaultsSide
        assertReflectionEquals("message", null, anyObject, IGNORE_DEFAULTS);  // Succeeds
        assertReflectionEquals(anyObject, null, IGNORE_DEFAULTS);  // Fails
        // END SNIPPET: ignoreDefaultsSide
    }

    @Test
    public void lenientDates() {
        // START SNIPPET: lenientDates
        Date actualDate = new Date(44444);
        Date expectedDate = new Date();
        assertReflectionEquals(expectedDate, actualDate, LENIENT_DATES);
        // END SNIPPET: lenientDates
    }

    @Test(expected = AssertionError.class)
    public void lenientEquals() {
        // START SNIPPET: lenientEquals
        List<Integer> myList = asList(3, 2, 1);
        assertLenientEquals(asList(1, 2, 3), myList);

        assertLenientEquals(null, "any");  // Succeeds
        assertLenientEquals("any", null);  // Fails
        // END SNIPPET: lenientEquals
    }

    @Test
    public void propertyAssertions() {
        User user = new User(1, "first", "last", new Address("First street", 2, "city"));
        // START SNIPPET: propertyAssertions
        assertPropertyLenientEquals("id", 1, user);
        assertPropertyLenientEquals("address.street", "First street", user);
        // END SNIPPET: propertyAssertions
    }

    @Test
    public void collectionPropertyAssertions() {
        User user1 = new User(1, "first", "last", new Address("First street", 2, "city"));
        User user2 = new User(2, "first", "last", new Address("Second street", 2, "city"));
        User user3 = new User(3, "first", "last", new Address("Third street", 2, "city"));
        List<User> users = asList(user1, user2, user3);
        // START SNIPPET: collectionPropertyAssertions
        assertPropertyLenientEquals("id", asList(1, 2, 3), users);
        assertPropertyLenientEquals("address.street", asList("First street", "Second street", "Third street"), users);
        // END SNIPPET: collectionPropertyAssertions
    }
}
