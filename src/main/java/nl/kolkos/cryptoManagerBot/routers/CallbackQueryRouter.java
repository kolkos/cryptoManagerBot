package nl.kolkos.cryptoManagerBot.routers;

import java.util.HashMap;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;

import nl.kolkos.cryptoManagerBot.commands.PeriodCommand;
import nl.kolkos.cryptoManagerBot.commands.PortfolioCommand;
import nl.kolkos.cryptoManagerBot.objects.CallbackQuery;

public class CallbackQueryRouter {
	private PortfolioCommand portfolioCommand = new PortfolioCommand();
	private PeriodCommand periodCommand = new PeriodCommand();
	
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
	
	public EditMessageText callbackDataForwarder(CallbackQuery callbackQuery) {
		HashMap<String, String> callbackDataMap = this.splitCallbackData(callbackQuery.getCallbackData());
		
		EditMessageText editMessageText = new EditMessageText()
                .setChatId(callbackQuery.getChatId())
                .setMessageId(callbackQuery.getMsgId())
                .setText("Unknown command");
		
		// check if the command option exists in the callbackDataMap
		if(!callbackDataMap.containsKey("command")) {
			editMessageText.setText("This button does not contain the required 'command' information");
			return editMessageText;
		}
		
		switch (callbackDataMap.get("command")) {
		case "generatePortfolioMenu":
			editMessageText = portfolioCommand.generatePortfolioMenu(callbackQuery);
			break;
		case "getPeriodOptions":
			editMessageText = periodCommand.generatePeriodMenu(callbackQuery, callbackDataMap);
			break;
		default:
			break;
		}
		
		
		return editMessageText;
	}
}
