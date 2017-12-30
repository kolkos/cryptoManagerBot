package nl.kolkos.cryptoManagerBot.commandHandlers;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;

public class CallbackQuery {
	private MenuItemService menuItemService = new MenuItemService();
	
	public SendMessage createChartMenu(long chatId) {
		SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText("Which chart you wish to create?");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Portfolio Chart").setCallbackData("command=getPortfolios"));
        rowsInline.add(rowInline);
        
        
        rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Wallet Chart").setCallbackData("command=getWallets"));
        rowsInline.add(rowInline);
        
        
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        
        return message;
	}
	
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
        		System.out.println("currentColumn = " + currentColumn);
        		System.out.println("counter = " + counter);
        		
        		if(currentColumn == columns) {
        			System.out.println("Create new row");
        			
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
}
