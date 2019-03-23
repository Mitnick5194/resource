package com.ajie.resource.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 *
 * @author niezhenjie
 *
 */
public class Main {

	public static void main(String[] args) {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("properties/db.properties");
		Properties pro = new Properties();
		try {
			pro.load(is);
			System.out.println(pro.getProperty("jdbc.url"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
