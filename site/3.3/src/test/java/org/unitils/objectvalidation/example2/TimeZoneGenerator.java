/*
 * Copyright (c) Smals
 */
package org.unitils.objectvalidation.example2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.unitils.objectvalidation.ObjectCreator;
import org.unitils.objectvalidation.objectcreator.generator.Generator;
import org.unitils.objectvalidation.utils.TreeNode;


/**
 * example creating a generator.
 * 
 * @author wiw
 * 
 * @since 3.3
 * 
 */
//START SNIPPET: objectvalidationExample2
public class TimeZoneGenerator implements Generator {
    private static final List<String> ZONES = Arrays.asList(TimeZone.getAvailableIDs());
    /**
     * @see org.unitils.objectvalidation.objectcreator.generator.Generator#generateObject(java.lang.Class, java.util.List, java.util.List, java.util.List)
     */
    @Override
    public Object generateObject(Class<?> clazz, List<Object> input, List<Class<?>> inputClasses, List<TreeNode> genericSubTypes, ObjectCreator objectCreator) throws Exception {
        if (clazz.equals(TimeZone.class)) {
            //get random time zone
              Random r = new Random();
              
              int zone = r.nextInt(ZONES.size()-1);
              return new SimpleTimeZone(r.nextInt(100), ZONES.get(zone));
          }
          return null;
    }

}
//END SNIPPET: objectvalidationExample2
