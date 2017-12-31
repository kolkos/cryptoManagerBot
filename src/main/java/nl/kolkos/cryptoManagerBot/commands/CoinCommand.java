package nl.kolkos.cryptoManagerBot.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import nl.kolkos.cryptoManagerBot.controllers.ApiRequestController;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.services.ChatService;

public class CoinCommand {
	private static final Logger LOG = LogManager.getLogger(CoinCommand.class);
	private ApiRequestController apiRequestController = new ApiRequestController();
	
	private ChatService chatService = new ChatService();
	
	public SendMessage coinCommandAdapter(Command command) {
		// check if the coin has an additional parameter
		String[] coinCommand = command.getCommand().split(" ");
		String messageText = "";
		if(coinCommand.length > 1) {
			String coinSymbol = coinCommand[1];
			messageText = this.runCoinCommand(command.getChatId(), coinSymbol);
		}else {
			messageText = this.runCoinCommand(command.getChatId());
		}
		
		// create the SendMessage
		SendMessage message = new SendMessage() 
                .setChatId(command.getChatId())
                .setText(messageText);
		
		return message;
	}
	
	private JSONObject runSingleCoinApiRequest(String apiKey, String coinSymbol) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/coin/symbol/" + coinSymbol.toUpperCase();
		
		// now run the api request
		JSONObject jsonObject = apiRequestController.jsonObjectRequest(url);
		
		return jsonObject;
	}
	
	private JSONArray runAllCoinApiRequest(String apiKey) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/coin";
		
		JSONArray jsonArray = apiRequestController.jsonArrayRequest(url);
		
		return jsonArray;
	}
	
	
	
	public String runCoinCommand(Long chatId) {
		// this will get all the coins
		LOG.trace("Handling /coin command for chat '{}'.", chatId);
		
		// check if the chat exists
		if(! chatService.checkIfChatIsRegistered(chatId)) {
			return "I'm sorry, this chat isn't registered yet. Please run the /start command first.";
		}
		
		String result = "";
		try {
			Chat chat = chatService.findChatByTelegramChatId(chatId);
			
			JSONArray jsonArray = this.runAllCoinApiRequest(chat.getApiKey());
			
			// loop through the array
			for(int i = 0; i < jsonArray.length(); i++) {
				// get the json object
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				// get the nested coin market cap coin object
				JSONObject jsonCMCObject = jsonObject.getJSONObject("coinMarketCapCoin");
				
				// now form the string
				String line = String.format("%s (%s): € %.2f\n", jsonCMCObject.getString("name"), jsonCMCObject.getString("symbol"), jsonObject.getDouble("currentCoinValue"));
				result += line;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.fatal("Error handling /coin command for chat '{}', error: '{}'", chatId, e);
			result = String.format("Error handling /coin command: '%s'", e.getMessage());
		}
		
		return result;
	}
	
	public String runCoinCommand(Long chatId, String coinSymbol) {
		// this will get a single coin result
		LOG.trace("Handling /coin command for chat '{}'. Coin: '{}'", chatId, coinSymbol);
		
		// check if the chat exists
		if(! chatService.checkIfChatIsRegistered(chatId)) {
			return "I'm sorry, this chat isn't registered yet. Please run the /start command first.";
		}
		
		String result = "";
		try {
			Chat chat = chatService.findChatByTelegramChatId(chatId);
			
			// run the api request
			JSONObject jsonObject = this.runSingleCoinApiRequest(chat.getApiKey(), coinSymbol);
			// check if the request contains the error key
			if(jsonObject.has("error")) {
				// exit this method with this error
				LOG.warn("Error handling /coin command for chat '{}', error: '{}'", chatId, jsonObject.getString("message"));
				return String.format("Something went wrong getting the Coin.\nError: '%s'", jsonObject.getString("message"));
			}
			
			// response is OK
			
			// get the nested coin market cap coin object
			JSONObject jsonCMCObject = jsonObject.getJSONObject("coinMarketCapCoin");
			
			
			// create the message
			
			result += String.format("Coin: %s (%s)\n", jsonCMCObject.getString("name"), jsonCMCObject.getString("symbol"));
			result += String.format("Current value: € %.2f", jsonObject.getDouble("currentCoinValue"));
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.fatal("Error handling /coin command for chat '{}', error: '{}'", chatId, e);
			result = String.format("Error handling /coin command: '%s'", e.getMessage());
		}

		
		return result;
	}
	
	
}
