package nl.kolkos.cryptoManagerBot.services;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.repositories.MenuItemRepository;

public class MenuItemService {
	public MenuItemRepository menuItemRepository = new MenuItemRepository();
	
	public List<MenuItem> getMenuItemsForCommand(String command){
		return menuItemRepository.getMenuItemsForCommand(command);
	}
	
	public SendMessage createMenuForCommand(String messageText, Command command, int columns) {
		SendMessage message = new SendMessage() 
                .setChatId(command.getChatId())
                .setText(messageText);
		
		// get the menu items
        List<MenuItem> menuItems = this.getMenuItemsForCommand(command.getCommand());
        
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = this.generateMenu(menuItems, columns);
        
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        
		return message;
	}
	
	public List<List<InlineKeyboardButton>> generateMenu(List<MenuItem> menuItems, int columns){
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		
		// now loop through the menu items
        int currentColumn = 0;

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

        }
        
        // add any leftover rows to the rows list
        rowsInline.add(rowInline);
		
		return rowsInline;
	}
}
