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

package org.unitils.io;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.io.annotation.FileContent;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public class IoModuleIntegrationTest extends UnitilsJUnit4 {

	@FileContent
	public String fileContent;

	@FileContent
	public Properties propertiesContent;

	@Test
	public void filledUpValuesTest() {
		Assert.assertEquals("testFile", fileContent);
		Assert.assertEquals("testFile", propertiesContent.getProperty("testFile"));
	}

}
