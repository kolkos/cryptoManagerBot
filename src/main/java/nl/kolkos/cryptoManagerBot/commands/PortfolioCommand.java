package nl.kolkos.cryptoManagerBot.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.controllers.ApiRequestController;
import nl.kolkos.cryptoManagerBot.objects.CallbackQuery;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.services.ChatService;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;

public class PortfolioCommand {
	private ChatService chatService = new ChatService();
	private MenuItemService menuItemService = new MenuItemService();
	private ApiRequestController apiRequestController = new ApiRequestController();
	
	private JSONArray getPortfolios(String apiKey) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/portfolio";
		
		JSONArray jsonArray = apiRequestController.jsonArrayRequest(url);
		
		return jsonArray;
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
	
	
	
}
