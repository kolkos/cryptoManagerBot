package nl.kolkos.cryptoManagerBot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import nl.kolkos.cryptoManagerBot.objects.CallbackQuery;
import nl.kolkos.cryptoManagerBot.objects.Interval;
import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.services.CallbackQueryService;
import nl.kolkos.cryptoManagerBot.services.IntervalService;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;

public class IntervalCommand {
	private static final Logger LOG = LogManager.getLogger(IntervalCommand.class);
	
	private CallbackQueryService callbackQueryService = new CallbackQueryService();
	private IntervalService intervalService = new IntervalService();
	private MenuItemService menuItemService = new MenuItemService();
	
	public EditMessageText generatePeriodMenu(CallbackQuery callbackQuery, HashMap<String, String> callbackDataMap) {
		// check if all the required fields are available
		List<String> requiredFields = new ArrayList<>();
		requiredFields.add("object");
		requiredFields.add("id");
		requiredFields.add("p");
		
		if(!callbackQueryService.checkAllRequiredFieldsAreSet(callbackDataMap, requiredFields)) {
			EditMessageText editMessageText = new EditMessageText()
	                .setChatId(callbackQuery.getChatId())
	                .setMessageId(callbackQuery.getMsgId())
	                .setText("Not all the required data is found. This button seems to be corrupt.");
			return editMessageText;
		}
		
		// now get the periods and create a menu
		EditMessageText intervalMenu = new EditMessageText()
                .setChatId(callbackQuery.getChatId())
                .setMessageId(callbackQuery.getMsgId())
                .setText("Please select a interval:");
		
		try {
			List<Interval> intervals = intervalService.getIntervalsForPeriod(callbackDataMap.get("p"));
			
			// create the menu via the Menu Item Service
	        // so we need to create a menu item list
	        List<MenuItem> menuItems = new ArrayList<>();
	        
	        // now loop through the intervals
	        for(Interval interval : intervals) {
	        		String callbackData = String.format("command=createChart,object=%s,id=%s,p=%s,i=%s", 
						callbackDataMap.get("object"),
						callbackDataMap.get("id"),
						interval.getPeriod(),
						interval.getValue());
				String text = interval.getText();
				
				// transform to a menu item
				MenuItem menuItem = new MenuItem();
				menuItem.setCallbackData(callbackData);
				menuItem.setText(text);
				
				menuItems.add(menuItem);
	        }
	        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
	        List<List<InlineKeyboardButton>> rowsInline = menuItemService.generateMenu(menuItems, 2);
			
			markupInline.setKeyboard(rowsInline);
			intervalMenu.setReplyMarkup(markupInline);
	        
		} catch (Exception e) {
			LOG.fatal("Error getting intervals: '{}'", e);
			intervalMenu.setText("Error getting intervals");
		}
		
		
		return intervalMenu;
	}
}
