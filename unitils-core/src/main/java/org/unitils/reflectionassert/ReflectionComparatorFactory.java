/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.comparator.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.unitils.core.util.CollectionUtils.asSet;
import static org.unitils.reflectionassert.ReflectionComparatorMode.*;

/**
 * A factory for creating a reflection comparator.
 * This will assemble the apropriate comparator chain and constructs a reflection comparator.
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
 * be considered equal.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorFactory {

    /**
     * The LenientDatesComparator singleton insance
     */
    protected static final Comparator LENIENT_DATES_COMPARATOR = new LenientDatesComparator();

    /**
     * The IgnoreDefaultsComparator singleton insance
     */
    protected static final Comparator IGNORE_DEFAULTS_COMPARATOR = new IgnoreDefaultsComparator();

    /**
     * The LenientNumberComparator singleton insance
     */
    protected static final Comparator LENIENT_NUMBER_COMPARATOR = new LenientNumberComparator();

    /**
     * The SimpleCasesComparatorsingleton insance
     */
    protected static final Comparator SIMPLE_CASES_COMPARATOR = new SimpleCasesComparator();

    /**
     * The LenientOrderCollectionComparator singleton insance
     */
    protected static final Comparator LENIENT_ORDER_COMPARATOR = new LenientOrderCollectionComparator();

    /**
     * The CollectionComparator singleton insance
     */
    protected static final Comparator COLLECTION_COMPARATOR = new CollectionComparator();

    /**
     * The MapComparator singleton insance
     */
    protected static final Comparator MAP_COMPARATOR = new MapComparator();

    /**
     * The HibernateProxyComparator singleton insance
     */
    protected static final Comparator HIBERNATE_PROXY_COMPARATOR = new HibernateProxyComparator();

    /**
     * The ObjectComparator singleton insance
     */
    protected static final Comparator OBJECT_COMPARATOR = new ObjectComparator();


    /**
     * Creates a reflection comparator for the given modes.
     * If no mode is given, a strict comparator will be created.
     *
     * @param modes The modes, null for strict comparison
     * @return The reflection comparator, not null
     */
    public static ReflectionComparator createRefectionComparator(ReflectionComparatorMode... modes) {
        List<Comparator> comparators = getComparatorChain(asSet(modes));
        return new ReflectionComparator(comparators);
    }


    /**
     * Creates a comparator chain for the given modes.
     * If no mode is given, a strict comparator will be created.
     *
     * @param modes The modes, null for strict comparison
     * @return The comparator chain, not null
     */
    protected static List<Comparator> getComparatorChain(Set<ReflectionComparatorMode> modes) {
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
        comparatorChain.add(HIBERNATE_PROXY_COMPARATOR);
        comparatorChain.add(OBJECT_COMPARATOR);
        return comparatorChain;
    }
}
