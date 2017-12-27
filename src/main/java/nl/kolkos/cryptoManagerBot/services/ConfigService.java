package nl.kolkos.cryptoManagerBot.services;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigService {
	public Properties loadConfig() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		
		Properties properties = new Properties();
		InputStream inputStream = classloader.getResourceAsStream("config.properties");
		
		properties.load(inputStream);
		
		
		return properties;
	}
}
