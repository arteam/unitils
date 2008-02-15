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
package org.unitils.reflectionassert.formatter.util;

import org.unitils.reflectionassert.difference.*;

import java.util.Map;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InnerDifferenceFinder {


    //todo review + javadoc
    public static Difference getInnerDifference(String fieldName, Difference difference) {
        return difference.accept(new InnerDifferenceVisitor(), fieldName);
    }


    protected static class InnerDifferenceVisitor implements DifferenceVisitor<Difference, String> {

        protected ObjectFormatter objectFormatter = new ObjectFormatter();

        public Difference visit(Difference difference, String argument) {
            return null;
        }

        public Difference visit(ObjectDifference objectDifference, String fieldName) {
            return objectDifference.getFieldDifferences().get(fieldName);
        }

        public Difference visit(MapDifference mapDifference, String keyString) {
            for (Map.Entry<Object, Difference> entry : mapDifference.getValueDifferences().entrySet()) {
                if (objectFormatter.format(entry.getKey()).equals(keyString)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        public Difference visit(CollectionDifference collectionDifference, String indexString) {
            return collectionDifference.getElementDifferences().get(new Integer(indexString));
        }

        public Difference visit(UnorderedCollectionDifference unorderedCollectionDifference, String indexString) {
            Map<Integer, Difference> differences = unorderedCollectionDifference.getElementDifferences().get(new Integer(indexString));
            if (differences == null || differences.isEmpty()) {
                return null;
            }
            return differences.values().iterator().next();
        }
    }


}