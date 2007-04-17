package org.unitils.reflectionassert;

import org.unitils.core.UnitilsException;
import org.unitils.util.CollectionUtils;


import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * todo javadoc
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionComparatorChainFactory {

    public static final ReflectionComparator STRICT_COMPARATOR = createComparatorChain(LenientNumberComparator.class,
            SimpleCasesComparator.class, CollectionComparator.class, MapComparator.class, ObjectComparator.class);

    public static final ReflectionComparator IGNOREDEFAULTS_COMPARATOR = createComparatorChain(IgnoreDefaultsComparator.class,
            LenientNumberComparator.class, SimpleCasesComparator.class, MapComparator.class, CollectionComparator.class,
            ObjectComparator.class);

    public static final ReflectionComparator LENIENTDATES_COMPARATOR = createComparatorChain(LenientDatesComparator.class,
            LenientNumberComparator.class, SimpleCasesComparator.class, CollectionComparator.class, MapComparator.class,
            ObjectComparator.class);

    public static final ReflectionComparator LENIENTORDER_COMPARATOR = createComparatorChain(LenientNumberComparator.class,
            SimpleCasesComparator.class, LenientOrderCollectionComparator.class, MapComparator.class, ObjectComparator.class);

    public static final ReflectionComparator IGNOREDEFAULTS_LENIENTDATES_COMPARATOR = createComparatorChain(LenientDatesComparator.class,
            IgnoreDefaultsComparator.class, LenientNumberComparator.class, SimpleCasesComparator.class, CollectionComparator.class,
            MapComparator.class, ObjectComparator.class);

    public static final ReflectionComparator IGNOREDEFAULTS_LENIENTORDER_COMPARATOR = createComparatorChain(IgnoreDefaultsComparator.class,
            SimpleCasesComparator.class, LenientNumberComparator.class, SimpleCasesComparator.class, LenientOrderCollectionComparator.class,
            MapComparator.class, ObjectComparator.class);

    public static final ReflectionComparator LENIENTDATES_LENIENTORDER_COMPARATOR = createComparatorChain(LenientDatesComparator.class,
            LenientNumberComparator.class, SimpleCasesComparator.class, LenientOrderCollectionComparator.class, MapComparator.class,
            ObjectComparator.class);

    public static final ReflectionComparator IGNOREDEFAULTS_LENIENTDATES_LENIENTORDER_COMPARATOR = createComparatorChain(LenientDatesComparator.class,
            IgnoreDefaultsComparator.class, LenientNumberComparator.class, SimpleCasesComparator.class, LenientOrderCollectionComparator.class,
            MapComparator.class, ObjectComparator.class);

    public static final Map<Set<ReflectionComparatorMode>, ReflectionComparator> COMPARATORMODES_COMPARATOR_MAP = new HashMap<Set<ReflectionComparatorMode>, ReflectionComparator>();

    static {
        COMPARATORMODES_COMPARATOR_MAP.put(new HashSet<ReflectionComparatorMode>(), STRICT_COMPARATOR);
        COMPARATORMODES_COMPARATOR_MAP.put(CollectionUtils.asSet(ReflectionComparatorMode.IGNORE_DEFAULTS), IGNOREDEFAULTS_COMPARATOR);
        COMPARATORMODES_COMPARATOR_MAP.put(CollectionUtils.asSet(ReflectionComparatorMode.LENIENT_DATES), LENIENTDATES_COMPARATOR);
        COMPARATORMODES_COMPARATOR_MAP.put(CollectionUtils.asSet(ReflectionComparatorMode.LENIENT_ORDER), LENIENTORDER_COMPARATOR);
        COMPARATORMODES_COMPARATOR_MAP.put(CollectionUtils.asSet(ReflectionComparatorMode.IGNORE_DEFAULTS, ReflectionComparatorMode.LENIENT_DATES), IGNOREDEFAULTS_LENIENTDATES_COMPARATOR);
        COMPARATORMODES_COMPARATOR_MAP.put(CollectionUtils.asSet(ReflectionComparatorMode.IGNORE_DEFAULTS, ReflectionComparatorMode.LENIENT_ORDER), IGNOREDEFAULTS_LENIENTORDER_COMPARATOR);
        COMPARATORMODES_COMPARATOR_MAP.put(CollectionUtils.asSet(ReflectionComparatorMode.LENIENT_DATES, ReflectionComparatorMode.LENIENT_ORDER), LENIENTDATES_LENIENTORDER_COMPARATOR);
        COMPARATORMODES_COMPARATOR_MAP.put(CollectionUtils.asSet(ReflectionComparatorMode.IGNORE_DEFAULTS, ReflectionComparatorMode.LENIENT_DATES, ReflectionComparatorMode.LENIENT_ORDER),
                IGNOREDEFAULTS_LENIENTDATES_LENIENTORDER_COMPARATOR);
    }


    public static ReflectionComparator createComparatorChain(Class<? extends ReflectionComparator>... comparatorClasses) {
        ReflectionComparator comparator = null;
        for (int i = comparatorClasses.length - 1; i >= 0; i--) {
            Class<? extends ReflectionComparator> comparatorClass = comparatorClasses[i];
            try {
                // Create a new comparator and chain it with the previous one. The first instance that is created is
                // the last instance of the chain, and is chained with null.
                comparator = comparatorClass.getConstructor(ReflectionComparator.class).newInstance(comparator);
            } catch (Exception e) {
                throw new UnitilsException("Error instantiating instance of " + comparatorClass.getSimpleName(), e);
            }
        }
        return comparator;
    }


    public static ReflectionComparator getComparatorChainForModes(ReflectionComparatorMode... modes) {
        return COMPARATORMODES_COMPARATOR_MAP.get(CollectionUtils.asSet(modes));
    }

}
