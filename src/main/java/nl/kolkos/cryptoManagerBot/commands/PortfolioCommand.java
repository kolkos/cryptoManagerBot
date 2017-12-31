package nl.kolkos.cryptoManagerBot.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.controllers.ApiRequestController;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.services.ChatService;

public class PortfolioCommand {
	private ChatService chatService = new ChatService();
	private ApiRequestController apiRequestController = new ApiRequestController();
	
	private JSONArray getPortfolios(String apiKey) throws Exception {
		// form the complete url
		apiRequestController.loadApiConfig();
		String url = apiRequestController.getApiBaseUrl() + apiKey + "/portfolio";
		
		JSONArray jsonArray = apiRequestController.jsonArrayRequest(url);
		
		return jsonArray;
	}
	
	public EditMessageText generatePortfolioMenu(long chatId, int msgId) {
		EditMessageText portfolioMenu = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(msgId)
                .setText("Please select a portfolio");
		
		// get the chat
		try {
			Chat chat = chatService.findChatByTelegramChatId(chatId);
			// get the portfolios from the api
			JSONArray jsonArray = this.getPortfolios(chat.getApiKey());
			
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
	        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
	        
			// loop through the array
			for(int i = 0; i < jsonArray.length(); i++) {
				// get the json object
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String text = jsonObject.getString("name");
				String callbackData = String.format("command=getPeriodOptions,object=portfolio,id=%d", jsonObject.getInt("id"));
				
				// create a menu item and add it to the list
				List<InlineKeyboardButton> rowInline = new ArrayList<>();
				rowInline.add(new InlineKeyboardButton().setText(text).setCallbackData(callbackData));
				rowsInline.add(rowInline);
				
			}
			
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
