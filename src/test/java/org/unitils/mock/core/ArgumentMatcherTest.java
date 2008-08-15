/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock.core;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.argumentmatcher.impl.RefEqArgumentMatcher;
import org.unitils.mock.argumentmatcher.impl.SameArgumentMatcher;
import org.unitils.mock.argumentmatcher.impl.EqualsArgumentMatcher;
import org.unitils.mock.argumentmatcher.impl.*;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;

public class ArgumentMatcherTest extends UnitilsJUnit4 {
	ArgumentMatcher notNullArgumentMatcher;
	ArgumentMatcher nullArgumentMatcher;
	ArgumentMatcher sameArgumentMatcher;
	ArgumentMatcher equalsArgumentMatcher;
	ArgumentMatcher refEqArgumentMatcher;
	ArgumentMatcher lenEqArgumentMatcher;
	
	TestObject object;
	TestObject sameAsObject;
	TestObject equalsObject;
	TestObject refEqObject;
	TestObject lenEqObject;
	TestObject differentObject;
	
	@Before
	public void setup() {
		List<String> innerList1 = Arrays.asList(new String[] { "1", "2" });
		List<String> innerList2 = Arrays.asList(new String[] { "1", "2" });
		List<String> innerList3 = Arrays.asList(new String[] { "2", "1" });
		List<String> innerList4 = Arrays.asList(new String[] { "3", "4" });
		
		object = new TestObject(innerList1);
		sameAsObject = object;
		equalsObject = new TestObject(innerList1);
		refEqObject = new TestObject(innerList2);
		lenEqObject = new TestObject(innerList3);
		differentObject = new TestObject(innerList4);
		
		notNullArgumentMatcher = new NotNullArgumentMatcher();
		nullArgumentMatcher = new NullArgumentMatcher();
		sameArgumentMatcher = new SameArgumentMatcher(object);
		equalsArgumentMatcher = new EqualsArgumentMatcher(object);
		refEqArgumentMatcher = new RefEqArgumentMatcher(object);
		lenEqArgumentMatcher = new LenEqArgumentMatcher(object);
	}
	
	@Test
	public void testNotNullArgumentMatcher() {
		Assert.assertTrue(notNullArgumentMatcher.matches(object));
		Assert.assertFalse(notNullArgumentMatcher.matches(null));
	}
	
	@Test
	public void testNullArgumentMatcher() {
		Assert.assertFalse(nullArgumentMatcher.matches(object));
		Assert.assertTrue(nullArgumentMatcher.matches(null));
	}
	
	@Test
	public void testSameArgumentMatcher() {
		Assert.assertTrue(sameArgumentMatcher.matches(sameAsObject));
		Assert.assertFalse(sameArgumentMatcher.matches(equalsObject));
	}
	
	@Test
	public void testEqualsArgumentMatcher() {
		Assert.assertTrue(equalsArgumentMatcher.matches(equalsObject));
		Assert.assertFalse(equalsArgumentMatcher.matches(refEqObject));
	}
	
	@Test
	public void testRefEqArgumentMatcher() {
		Assert.assertTrue(refEqArgumentMatcher.matches(refEqObject));
		Assert.assertFalse(refEqArgumentMatcher.matches(lenEqObject));
	}
	
	@Test
	public void testLenEqArgumentMatcher() {
		Assert.assertTrue(lenEqArgumentMatcher.matches(lenEqObject));
		Assert.assertFalse(lenEqArgumentMatcher.matches(differentObject));
	}
	
	public static class TestObject {
		private final List<String> testList;
		public TestObject(List<String> testList) {
			this.testList = testList;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			if(!(obj instanceof TestObject)) {
				return false;
			}
			return ((TestObject)obj).testList == testList;
		}
	}
}
