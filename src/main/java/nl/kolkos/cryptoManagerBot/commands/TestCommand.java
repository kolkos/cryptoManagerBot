package nl.kolkos.cryptoManagerBot.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import nl.kolkos.cryptoManagerBot.controllers.ApiRequestController;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.services.ChatService;

public class TestCommand {
	private static final Logger LOG = LogManager.getLogger(TestCommand.class);
	private ApiRequestController apiRequestController = new ApiRequestController();
	
	private ChatService chatService = new ChatService();
	
	
	public JSONObject runTestApiKeyApiRequest(String apiKey) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/test";
		
		// now run the api request
		JSONObject jsonObject = apiRequestController.jsonObjectRequest(url);
		
		return jsonObject;
	}
	
	public String runTestCommand(long chatId) {
		LOG.trace("Handling /test command for chat '{}'", chatId);
		
		// check if the chat exists
		if(! chatService.checkIfChatIsRegistered(chatId)) {
			return "I'm sorry, this chat isn't registered yet. Please run the /start command first.";
		}
				
		// get the Chat object
		String result = "";
		try {
			Chat chat = chatService.findChatByTelegramChatId(chatId);
			
			// get the status
			JSONObject jsonObject = this.runTestApiKeyApiRequest(chat.getApiKey());
			String status = jsonObject.getString("status");
			if(status.equals("error")) {
				LOG.warn("Error for API key '{}', status: '{}', reason: '{}'", chat.getApiKey(), status, jsonObject.getString("reason"));
				result += String.format("Error found while validating the API key '%s'.\nError: '%s'.", chat.getApiKey(), jsonObject.getString("reason"));
			}else {
				LOG.info("API key '{}' verified successfully, status: '{}'", chat.getApiKey(), status);
				result += String.format("Succesfull verified API key: '%s'.\nAPI Status: '%s'", chat.getApiKey(), status);
			}
			
		} catch (Exception e) {
			LOG.fatal("Error handling /test command for chat '{}', error: '{}'", chatId, e);
			result = String.format("Error handling /test command: '%s'", e.getMessage());
			
		}
		
		return result;
	}

	
}
