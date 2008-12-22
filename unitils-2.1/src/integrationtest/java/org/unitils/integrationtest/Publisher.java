package org.unitils.integrationtest;

import java.util.HashSet;
import java.util.Set;

public class Publisher {

	private Set<Subscriber> subscribers = new HashSet<Subscriber>();
	
	public void addSubscriber(Subscriber subscriber) {
		subscribers.add(subscriber);
	}
	
	public void publish(String message) {
		for (Subscriber subscriber : subscribers) {
			subscriber.receive(message);
		}
	}
}
