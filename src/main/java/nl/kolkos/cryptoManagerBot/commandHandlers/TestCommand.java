package nl.kolkos.cryptoManagerBot.commandHandlers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import nl.kolkos.cryptoManagerBot.controllers.ApiRequestController;

public class TestCommand {
	private static final Logger LOG = LogManager.getLogger(TestCommand.class);
	private ApiRequestController apiRequestController = new ApiRequestController();
	
	
	public JSONObject runTestApiKeyApiRequest(String apiKey) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/test";
		
		// now run the api request
		JSONObject jsonObject = apiRequestController.jsonObjectRequest(url);
		
		return jsonObject;
	}
	
	public String runTestCommand(String apiKey) {
		LOG.trace("Handling /test command for API key '{}'", apiKey);
		String result = "";
		// run the api command
		try {
			JSONObject jsonObject = this.runTestApiKeyApiRequest(apiKey);
			
			// get the status
			String status = jsonObject.getString("status");
			if(status.equals("error")) {
				LOG.warn("Error for API key '{}', status: '{}', reason: '{}'", apiKey, status, jsonObject.getString("reason"));
				result += String.format("API Status: %s\nAPI Error:%s", status, jsonObject.getString("reason"));
			}else {
				LOG.info("API key '{}' verified successfully, status: '{}'", apiKey, status);
				result += String.format("API Status: %s", status);
			}
			
			
		} catch (Exception e) {
			LOG.fatal("Error handling API request for key '{}', error: '{}'", apiKey, e);
			result += String.format("API Status: %s\nAPI Error:%s", "ERROR", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		
		
		return result;
	}
	
	
}
