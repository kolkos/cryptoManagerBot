package nl.kolkos.cryptoManagerBot.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.kolkos.cryptoManagerBot.services.ConfigService;

public class ApiRequestController {
	private static final Logger LOG = LogManager.getLogger(ApiRequestController.class);
	
	private String apiBaseUrl;
	
	
	public String getApiBaseUrl() {
		return apiBaseUrl;
	}


	public void setApiBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}


	public void loadApiConfig() throws IOException {
		LOG.trace("Load API configuration");
		
		ConfigService configService = new ConfigService();
		Properties properties = configService.loadConfig();
		
		String host = properties.getProperty("cryptoManager.host");
		String port = properties.getProperty("cryptoManager.port");
		String protocol = properties.getProperty("cryptoManager.protocol");

		// form the base url
		String url = String.format("%s://%s:%s/api/request/", protocol, host, port);
		
		this.setApiBaseUrl(url);
		
		LOG.info("API Base URL: {}", this.getApiBaseUrl());
		LOG.trace("Finished loading API configuration");		
	}
	
	/**
	 * Requests a JSON array object
	 * @param urlString the api url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public JSONArray jsonArrayRequest(String urlString) throws IOException, JSONException {
		URL url = new URL(urlString);
		
		// read from the URL
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONArray jsonArray = new JSONArray(str);
	    
	    return jsonArray;
	}
	
	/**
	 * The method does a request on a url and gets the results as a JSON Object
	 * @param urlString the api url
	 * @return a JSON object containing the results
	 * @throws Exception
	 */
	public JSONObject jsonObjectRequest(String urlString) throws Exception {
		URL url = new URL(urlString);
		
		// read from the URL
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    // build a JSON object
	    JSONObject jsonObject = new JSONObject(str);
	    
	    return jsonObject;
	}
	
	
	
}
