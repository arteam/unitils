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
package org.unitils.dbmaintainer.version;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class VersionTest {

	@Test
	public void equalVersions() {
		Version v1 = new Version(Arrays.asList(1L, 2L, 3L));
		Version v2 = new Version(Arrays.asList(1L, 2L, 3L));
		assertEquals(v1, v2);
		assertEquals(0, v1.compareTo(v2));
	}
	
	@Test
	public void sameLenghtDifferentVersion() {
		Version v1 = new Version(Arrays.asList(1L, 2L, 3L));
		Version v2 = new Version(Arrays.asList(1L, 2L, 4L));
		assertEquals(-1, v1.compareTo(v2));
		assertEquals(1, v2.compareTo(v1));
	}
	
	@Test
	public void differentLength() {
		Version v1 = new Version(Arrays.asList(1L, 2L, 3L));
		Version v2 = new Version(Arrays.asList(1L, 2L, 3L, 1L));
		assertEquals(-1, v1.compareTo(v2));
		assertEquals(1, v2.compareTo(v1));
	}
	
	@Test 
	public void nullVersionPart() {
		Version v1 = new Version(Arrays.asList(1L, 2L, 3L));
		Version v2 = new Version(Arrays.asList(1L, null));
		assertEquals(-1, v1.compareTo(v2));
		assertEquals(1, v2.compareTo(v1));
	}
}
