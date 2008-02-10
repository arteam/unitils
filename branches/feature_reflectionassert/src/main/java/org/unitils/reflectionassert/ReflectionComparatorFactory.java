/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.reflectionassert;

import org.unitils.reflectionassert.comparator.impl.*;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.unitils.reflectionassert.ReflectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.*;
import static org.unitils.util.CollectionUtils.asSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * todo review javadoc
 * <p/>
 * This class functions as a factory for reflection comparator chains. A reflection comparator chain is a chain of
 * instances of {@link ReflectionComparator} subclasses. A reflection comparator chain will compare two objects with
 * each other using reflection, and returns a {@link org.unitils.reflectionassert.difference.Difference} object. You can use a reflection
 * comparator chain by invoking {@link ReflectionComparator#getDifference(Object,Object)} on the root of the chain.
 * <p/>
 * The {@link Object#equals} method is often used for business logic equality checking. The {@link Object#equals} method
 * can for example return true when the id fields of 2 instances have equal values, no matter what the values of the
 * other fields are. A reflection comparator chain offers another way to check equality of objects.
 * <p/>
 * A reflection comparator chain will use reflection to get and compare the values of all fields in
 * the objects. If field contains another object, the same reflection comparison will be done recursively on these inner
 * objects. All fields in superclasses will also be compared using reflection. Static and transient fields will be ignored.
 * <p/>
 * As an exception, the {@link Object#equals} method will be called instead of using reflection on all
 * java.lang.* type field values. Eg a field of type java.lang.Integer will be compared using its equals method. No
 * superclass comparison is done on java.lang.* type classes. Eg the java.lang.Object class fields will not be compared.
 * <p/>
 * If an object is an array or a collection, all its elements will be traversed and compared with the other array or
 * collection in the same way using reflection. The actual type of collection or whether a collection is compared with
 * an array is not important. It will only go through the array or collection and compare the elements. For example, an
 * Arraylist can be compared with an array or a LinkedList.
 * <p/>
 * By default, a strict comparison is performed, but if needed, some leniency can be configured by setting one or more
 * comparator modes: <ul>
 * <li>ignore defaults: all fields that have a default java value for the left object will be ignored. Eg if
 * the left object contains an int field with value 0 it will not be compared to the value of the right object.</li>
 * <li>lenient dates: only check whether both Date objects contain a value or not, the value itself
 * is not compared. Eg. if the left object contained a date with value 1-1-2006 and the right object contained a date
 * with value 2-2-2006 they would still be considered equal.</li>
 * <li>lenient order: only check whether both collections or arrays contain the same value, the actual order of the
 * values is not compared. Eg. if the left object is int[]{ 1, 2} and the right value is int[]{2, 1} they would still
 * be considered equal.</li>
 * </ul>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionComparatorFactory {


    // todo javadoc
    public static final Comparator LENIENT_DATES_COMPARATOR = new LenientDatesComparator();

    public static final Comparator IGNORE_DEFAULTS_COMPARATOR = new IgnoreDefaultsComparator();

    public static final Comparator LENIENT_NUMBER_COMPARATOR = new LenientNumberComparator();

    public static final Comparator SIMPLE_CASES_COMPARATOR = new SimpleCasesComparator();

    public static final Comparator LENIENT_ORDER_COMPARATOR = new LenientOrderCollectionComparator();

    public static final Comparator COLLECTION_COMPARATOR = new CollectionComparator();

    public static final Comparator MAP_COMPARATOR = new MapComparator();

    public static final Comparator OBJECT_COMPARATOR = new ObjectComparator();


    // todo javadoc
    public static ReflectionComparator createRefectionComparator(ReflectionComparatorMode... modes) {
        List<Comparator> comparators = getComparatorChain(asSet(modes));
        return new ReflectionComparator(comparators);
    }


    // todo javadoc
    public static List<Comparator> getComparatorChain(Set<ReflectionComparatorMode> modes) {
        List<Comparator> comparatorChain = new ArrayList<Comparator>();
        if (modes.contains(LENIENT_DATES)) {
            comparatorChain.add(LENIENT_DATES_COMPARATOR);
        }
        if (modes.contains(IGNORE_DEFAULTS)) {
            comparatorChain.add(IGNORE_DEFAULTS_COMPARATOR);
        }
        comparatorChain.add(LENIENT_NUMBER_COMPARATOR);
        comparatorChain.add(SIMPLE_CASES_COMPARATOR);
        if (modes.contains(LENIENT_ORDER)) {
            comparatorChain.add(LENIENT_ORDER_COMPARATOR);
        } else {
            comparatorChain.add(COLLECTION_COMPARATOR);
        }
        comparatorChain.add(MAP_COMPARATOR);
        comparatorChain.add(OBJECT_COMPARATOR);
        return comparatorChain;
    }
}
