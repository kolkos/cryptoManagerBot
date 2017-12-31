package nl.kolkos.cryptoManagerBot.commandHandlers;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;

public class CallbackQueryCommand {
	private MenuItemService menuItemService = new MenuItemService();
	private PortfolioCommand portfolioCommand = new PortfolioCommand();
	
	
	public SendMessage createMenuForCommand(String messageText,String command, long chatId, int columns) {
		SendMessage message = new SendMessage() 
                .setChatId(chatId)
                .setText(messageText);
		
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        
        // get the menu items
        List<MenuItem> menuItems = menuItemService.getMenuItemsForCommand(command);
        
        // now loop through the menu items
        int currentColumn = 0;
        int counter = 0;
        //boolean createNewRow = false;
        for(MenuItem menuItem : menuItems) {
        		// check if a new column needs to be created
        		
        		if(currentColumn == columns) {
        			// add the current row to the rows list
        			rowsInline.add(rowInline);
        			
        			// now create new row
        			rowInline = new ArrayList<>();
        			
        			// reset the counter
        			currentColumn = 1;
        		}else {
        			currentColumn++;
        		}
        		
        		// add to the row
        		rowInline.add(new InlineKeyboardButton().setText(menuItem.getText()).setCallbackData(menuItem.getCallbackData()));
        		
        		
        		counter++;
        }
        
        // add any leftover rows to the rows list
        rowsInline.add(rowInline);
        
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        
		
		
		return message;
	}
	
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
