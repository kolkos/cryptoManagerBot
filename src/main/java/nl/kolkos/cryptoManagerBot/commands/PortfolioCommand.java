package nl.kolkos.cryptoManagerBot.commands;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.controllers.ApiRequestController;
import nl.kolkos.cryptoManagerBot.objects.CallbackQuery;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.services.CallbackQueryService;
import nl.kolkos.cryptoManagerBot.services.ChatService;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;

public class PortfolioCommand {
	private ChatService chatService = new ChatService();
	private MenuItemService menuItemService = new MenuItemService();
	private CallbackQueryService callbackQueryService = new CallbackQueryService();
	private ApiRequestController apiRequestController = new ApiRequestController();
	
	
	
	private JSONArray getPortfolios(String apiKey) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/portfolio";
		
		JSONArray jsonArray = apiRequestController.jsonArrayRequest(url);
		
		return jsonArray;
	}
	
	private JSONObject getPortfolioValue(String apiKey, String portfolioId) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/portfolio/" + portfolioId;
		
		JSONObject jsonObject = apiRequestController.jsonObjectRequest(url);
		
		return jsonObject;
	}
	
	public SendMessage generatePortfolioMenu(Command command) {
		// create the SendMessage
		SendMessage message = new SendMessage() 
                .setChatId(command.getChatId())
                .setText("Choose one of the following portfolios:");
		
		try {
			Chat chat = chatService.findChatByTelegramChatId(command.getChatId());
			// create the menu via the Menu Item Service
	        // so we need to create a menu item list
	        List<MenuItem> menuItems = new ArrayList<>();
	        
	        // get the portfolios from the api
	     	JSONArray jsonArray = this.getPortfolios(chat.getApiKey());
	        
	        // loop through the array
			for(int i = 0; i < jsonArray.length(); i++) {
				// get the json object
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String text = jsonObject.getString("name");
				String callbackData = String.format("command=getPortfolioValue,id=%d", jsonObject.getInt("id"));
				
				MenuItem menuItem = new MenuItem();
				menuItem.setCallbackData(callbackData);
				menuItem.setText(text);
				menuItems.add(menuItem);
				
			}
			
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
	        List<List<InlineKeyboardButton>> rowsInline = menuItemService.generateMenu(menuItems, 1);
			
			markupInline.setKeyboard(rowsInline);
			message.setReplyMarkup(markupInline);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			message.setText(String.format("An error occured creating the portfolio menu: '%s'", e.getMessage()));
		}
		
		
		return message;
	}
	
	public EditMessageText generatePortfolioMenu(CallbackQuery callbackQuery) {
		EditMessageText portfolioMenu = new EditMessageText()
                .setChatId(callbackQuery.getChatId())
                .setMessageId(callbackQuery.getMsgId())
                .setText("Please select a portfolio");
		
		// get the chat
		try {
			Chat chat = chatService.findChatByTelegramChatId(callbackQuery.getChatId());
			// get the portfolios from the api
			JSONArray jsonArray = this.getPortfolios(chat.getApiKey());
			
	        // create the menu via the Menu Item Service
	        // so we need to create a menu item list
	        List<MenuItem> menuItems = new ArrayList<>();
	        
	        
	        
			// loop through the array
			for(int i = 0; i < jsonArray.length(); i++) {
				// get the json object
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String text = jsonObject.getString("name");
				String callbackData = String.format("command=getPeriodOptions,object=portfolio,id=%d", jsonObject.getInt("id"));
				
				MenuItem menuItem = new MenuItem();
				menuItem.setCallbackData(callbackData);
				menuItem.setText(text);
				menuItems.add(menuItem);
				
			}
			
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
	        List<List<InlineKeyboardButton>> rowsInline = menuItemService.generateMenu(menuItems, 1);
			
			markupInline.setKeyboard(rowsInline);
			portfolioMenu.setReplyMarkup(markupInline);
	        
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			portfolioMenu.setText(String.format("An error occured creating the portfolio menu: '%s'", e.getMessage()));
		}
		
		return portfolioMenu;
	}
	
	public EditMessageText generatePortfolioValueMessage(CallbackQuery callbackQuery, HashMap<String, String> callbackDataMap) {
		EditMessageText editMessageText = new EditMessageText()
                .setChatId(callbackQuery.getChatId())
                .setMessageId(callbackQuery.getMsgId())
                .setText("Defailt message");
		
		// check if all the required fields are available
		List<String> requiredFields = new ArrayList<>();
		requiredFields.add("id");
		
		if(!callbackQueryService.checkAllRequiredFieldsAreSet(callbackDataMap, requiredFields)) {
			editMessageText.setText("Not all the required fields are filled. This button may be corrupt.");
			return editMessageText;
		}
		
		try {
			Chat chat = chatService.findChatByTelegramChatId(callbackQuery.getChatId());
			
			JSONObject jsonObject = this.getPortfolioValue(chat.getApiKey(), callbackDataMap.get("id"));
			
			String portfolioName = jsonObject.getString("name");
			double currentTotalPortfolioValue = jsonObject.getDouble("currentTotalPortfolioValue");
			double currentTotalPortfolioInvestment = jsonObject.getDouble("currentTotalPortfolioInvestment");
			double currentTotalPortfolioProfitLoss = jsonObject.getDouble("currentTotalPortfolioProfitLoss");
			double currentTotalPortfolioROI = jsonObject.getDouble("currentTotalPortfolioROI") * 100;
			
			String messageText = String.format("The current value of the portfolio '%s':\n\n", portfolioName);
			messageText += String.format("Total portfolio value: € %.2f\n", currentTotalPortfolioValue);
			messageText += String.format("The investment is: € %.2f\n", currentTotalPortfolioInvestment);
			messageText += String.format("This makes a total profit/loss of: € %.2f\n", currentTotalPortfolioProfitLoss);
			messageText += String.format("This is a Return of Investment of %.2f%%\n\n", currentTotalPortfolioROI);
			
			// now get the wallet values
			JSONArray wallets = jsonObject.getJSONArray("wallets");
			messageText += "This portfolio contains the following wallets:\n";
			
			for(int i = 0; i < wallets.length(); i++) {
				// get the json object
				JSONObject wallet = wallets.getJSONObject(i);
				
				String walletAddress = wallet.getString("censoredWalletAddress");
				String coinSymbol = wallet.getJSONObject("coin").getJSONObject("coinMarketCapCoin").getString("symbol");
				
				double currentWalletAmount = wallet.getDouble("currentWalletAmount");
				double currentWalletValue = wallet.getDouble("currentWalletValue");
				double currentWalletInvestment = wallet.getDouble("currentWalletInvestment");
				double currentWalletProfitLoss = wallet.getDouble("currentWalletProfitLoss");
				double currentWalletROI = wallet.getDouble("currentWalletROI") * 100;
				
				
				messageText += String.format("  %s (%s):\n", walletAddress, coinSymbol);
				messageText += String.format("    This wallet contains %.8f %s\n", currentWalletAmount, coinSymbol);
				messageText += String.format("    The value of this wallet is € %.2f\n", currentWalletValue);
				messageText += String.format("    The investment is € %.2f \n", currentWalletInvestment);
				messageText += String.format("    Profit/loss: € %.2f\n", currentWalletProfitLoss);
				messageText += String.format("    ROI: %.2f%%\n\n", currentWalletROI);
			}
			
			editMessageText.setText(messageText);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			editMessageText.setText(String.format("An error occured getting the portfolio value: '%s'", e.getMessage()));
		}
		
		return editMessageText;
	}
	
	
	
}
