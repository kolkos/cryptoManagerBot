package nl.kolkos.cryptoManagerBot.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.controllers.ApiRequestController;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.services.ChatService;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;

public class WalletCommand {
	private ApiRequestController apiRequestController = new ApiRequestController();
	private ChatService chatService = new ChatService();
	private MenuItemService menuItemService = new MenuItemService();
	
	private JSONArray getWallets(String apiKey) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/wallet";
		
		JSONArray jsonArray = apiRequestController.jsonArrayRequest(url);
		
		return jsonArray;
	}
	
	public SendMessage generateWalletMenu(Command command) {
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
	     	JSONArray jsonArray = this.getWallets(chat.getApiKey());
	        
	        // loop through the array
			for(int i = 0; i < jsonArray.length(); i++) {
				// get the json object
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String censoredWalletAddress = jsonObject.getString("censoredWalletAddress");
				String coinSymbol = jsonObject.getJSONObject("coin").getJSONObject("coinMarketCapCoin").getString("symbol");
				String text = String.format("%s (%s)", censoredWalletAddress, coinSymbol);
				
				String callbackData = String.format("command=getWalletValue,id=%d", jsonObject.getInt("id"));
				
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
}
