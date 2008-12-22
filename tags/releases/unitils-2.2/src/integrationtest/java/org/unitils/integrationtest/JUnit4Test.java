package org.unitils.integrationtest;

import org.junit.Test;

public class JUnit4Test {

	
	
	public JUnit4Test() {
		super();
		System.out.println("Constructor");
	}

	@Test public void test1() {
		System.out.println("test1");
	}
	
	@Test public void test2() {
		System.out.println("test2");
	}
}
