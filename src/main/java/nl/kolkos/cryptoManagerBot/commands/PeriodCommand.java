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
import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.objects.Period;
import nl.kolkos.cryptoManagerBot.services.CallbackQueryService;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;
import nl.kolkos.cryptoManagerBot.services.PeriodService;

public class PeriodCommand {
	private static final Logger LOG = LogManager.getLogger(PeriodCommand.class);
	
	private CallbackQueryService callbackQueryService = new CallbackQueryService();
	private PeriodService periodService = new PeriodService();
	private MenuItemService menuItemService = new MenuItemService();
	
	public EditMessageText generatePeriodMenu(CallbackQuery callbackQuery, HashMap<String, String> callbackDataMap) {
		// check if all the required fields are available
		List<String> requiredFields = new ArrayList<>();
		requiredFields.add("object");
		requiredFields.add("id");
		
		if(!callbackQueryService.checkAllRequiredFieldsAreSet(callbackDataMap, requiredFields)) {
			EditMessageText editMessageText = new EditMessageText()
	                .setChatId(callbackQuery.getChatId())
	                .setMessageId(callbackQuery.getMsgId())
	                .setText("Not all the required data is found. This button seems to be corrupt.");
			return editMessageText;
		}
		
		// now get the periods and create a menu
		EditMessageText periodMenu = new EditMessageText()
                .setChatId(callbackQuery.getChatId())
                .setMessageId(callbackQuery.getMsgId())
                .setText("Please select a period");
		try {
			// create the menu via the Menu Item Service
	        // so we need to create a menu item list
	        List<MenuItem> menuItems = new ArrayList<>();
			
			List<Period> periods = periodService.getPeriods();
			// loop through the periods
			for(Period period : periods) {
				String callbackData = String.format("command=getIntervals,object=%s,id=%s,period=%s", 
						callbackDataMap.get("object"),
						callbackDataMap.get("id"),
						period.getValue());
				String text = period.getText();
				
				// transform to a menu item
				MenuItem menuItem = new MenuItem();
				menuItem.setCallbackData(callbackData);
				menuItem.setText(text);
				
				menuItems.add(menuItem);
			}
			
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
	        List<List<InlineKeyboardButton>> rowsInline = menuItemService.generateMenu(menuItems, 2);
			
			markupInline.setKeyboard(rowsInline);
			periodMenu.setReplyMarkup(markupInline);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.fatal("Error getting periods: '{}'", e);
			periodMenu.setText("Error getting periods");
		}
		
		return periodMenu;
	}
}
