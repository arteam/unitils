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
package org.unitils.mock;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.AfterCreateMock;
import org.unitils.mock.annotation.Mock;
import org.unitils.mock.annotation.PartialMock;
import static org.unitils.mock.MockUnitils.*;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockTest extends UnitilsJUnit4 {

	@Mock Outputter outputter;
	
	@PartialMock MessageFactory messageFactory;
	
	HelloWorld helloWorld;
	
	String person = "Jos";
	
	int number = 5;
	
	@AfterCreateMock
	public void afterCreateMock(Object mock, String name, Class<?> type) {
		System.out.println(name + " " + type);
	}
	
	@Before
	public void setup() {
		helloWorld = new HelloWorld(outputter);
	}
	
	@Test
	public void mockTest() {
		//mock(messageFactory).returns("hello world").getMessage(null, eq("sdfsd"));
		helloWorld.sayHello(messageFactory, person);
		
		assertInvoked(outputter).output(messageFactory, 4);
//		assertInvoked(outputter).output(eq("hello world"));
	}
	
	static class HelloWorld {
		
		private Outputter outputter;
		
		public HelloWorld(Outputter outputter) {
			super();
			this.outputter = outputter;
		}

		public void sayHello(MessageFactory messageFactory, String person) {
			outputter.output(messageFactory, messageFactory.getMessage(person));
		}
	}
	
	public static class Outputter {
		
		public void output(MessageFactory messageFactory, int arg1) {
			System.out.println(arg1);
		}
	}
	
	public static class MessageFactory {
		
		public int getMessage(String person) {
			return 5;
		}
	}
}
