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
package org.unitils.mock;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.AfterCreateMock;
import org.unitils.mock.annotation.Mock;

import static org.unitils.mock.MockUnitils.*;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockTest extends UnitilsJUnit4 {

	@Mock Outputter outputter;
	
	@Mock MessageFactory messageFactory;
	
	HelloWorld helloWorld;
	
	@AfterCreateMock
	public void afterCreateMock(Object mock, String name, Class<?> type) {
		System.out.println(mock + " " + name + " " + type);
	}
	
	@Before
	public void setup() {
		helloWorld = new HelloWorld(outputter, messageFactory);
	}
	
	@Test
	public void mockTest() {
		mock(messageFactory).returns("hello world").getMessage(null, eq("sdfsd"));
		helloWorld.sayHello();
		assertInvoked(outputter).output(eq("hello world"));
//		assertInvoked(outputter).output(eq("hello world"));
	}
	
	static class HelloWorld {
		
		private Outputter outputter;
		
		private MessageFactory messageFactory;
		
		public HelloWorld(Outputter outputter, MessageFactory messageFactory) {
			super();
			this.outputter = outputter;
			this.messageFactory = messageFactory;
		}

		public void sayHello() {
			outputter.output(messageFactory.getMessage("ba", "boe"));
		}
	}
	
	static class Outputter {
		
		public void output(String arg1) {
			System.out.println(arg1);
		}
	}
	
	static class MessageFactory {
		
		public String getMessage(String arg1, String arg2) {
			return "boe";
		}
	}
}
