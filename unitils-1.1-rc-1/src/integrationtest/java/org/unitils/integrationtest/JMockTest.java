package org.unitils.integrationtest;

import org.junit.Test;
import 	org.jmock.*;

public class JMockTest {

	Mockery context = new Mockery();
	
	@Test
	public void testSubscribers() {
		final Subscriber subscriber = context.mock(Subscriber.class);
		
		Publisher publisher = new Publisher();
		publisher.addSubscriber(subscriber);
		
		context.checking(new Expectations() {{
			one(subscriber).receive("Test1");
			one(subscriber).receive("Test2");
		}});
		
		publisher.publish("Test1");
		publisher.publish("Test3");
		publisher.publish("Test4");
		
		context.assertIsSatisfied();
	}
}
