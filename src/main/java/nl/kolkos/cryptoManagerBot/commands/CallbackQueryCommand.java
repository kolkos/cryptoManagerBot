package nl.kolkos.cryptoManagerBot.commands;

import java.util.HashMap;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;


public class CallbackQueryCommand {
	
	private PortfolioCommand portfolioCommand = new PortfolioCommand();
	
	
	
	
	private HashMap<String, String> splitCallbackData(String callbackData){
		HashMap<String, String> callbackDataMap = new HashMap<>();
		
		// first split the string to key/value pairs
		String[] keyValues = callbackData.split(",");
		// now loop through the key/values
		for(String keyValue : keyValues) {
			// split to key/values
			String[] temp = keyValue.split("=");
			String key = temp[0];
			String value = temp[1];
			callbackDataMap.put(key, value);
		}
		
		return callbackDataMap;
	}
	
	
	public EditMessageText callbackDataForwarder(String callbackData, long chatId, int msgId ) {
		HashMap<String, String> callbackDataMap = this.splitCallbackData(callbackData);
		
		EditMessageText editMessageText = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(msgId)
                .setText("Unknown command");
		
		// check if the command option exists in the callbackDataMap
		if(!callbackDataMap.containsKey("command")) {
			editMessageText.setText("This button does not contain the required 'command' information");
			return editMessageText;
		}
		
		switch (callbackDataMap.get("command")) {
		case "generatePortfolioMenu":
			editMessageText = portfolioCommand.generatePortfolioMenu(chatId, msgId);
			break;

		default:
			break;
		}
		
		
		return editMessageText;
	}
}
