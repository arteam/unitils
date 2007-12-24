package org.unitils.integrationtest;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.derby.impl.sql.compile.GetCurrentConnectionNode;
import org.hibernate.ejb.Ejb3Configuration;

public class JpaTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("org/unitils/integrationtest/dao/jpa/persistence-test.xml");
		System.out.println(url);
	}

}
