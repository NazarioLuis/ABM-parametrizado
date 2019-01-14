package com.autoabm.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Connexion {
	
	private static SessionFactory sessionFactory;

	public static void crear() {
		StandardServiceRegistry serviceRegistry = 
				new StandardServiceRegistryBuilder()
				.configure()
				.build();
		
		try {
			sessionFactory = 
					new MetadataSources(serviceRegistry)
						.buildMetadata()
						.buildSessionFactory();
			sessionFactory.openSession();
		} catch (Exception e) {
			System.out.println("Se produjo un error al crear la conexion\n");
			e.printStackTrace();
			StandardServiceRegistryBuilder.destroy(serviceRegistry);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			crear();
		}
		return sessionFactory;
	}

}
