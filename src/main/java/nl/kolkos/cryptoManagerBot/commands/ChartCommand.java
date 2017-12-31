package nl.kolkos.cryptoManagerBot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.services.MenuItemService;

public class ChartCommand {
	private static final Logger LOG = LogManager.getLogger(ChartCommand.class);
	private MenuItemService menuItemService = new MenuItemService();
	
	public SendMessage chartCommandAdapter(Command command) {
		LOG.trace("Handling /chart command");
		// create the SendMessage
		SendMessage message = new SendMessage() 
                .setChatId(command.getChatId())
                .setText("default message");
		
		message = menuItemService.createMenuForCommand("Please select a portfolio:", command, 1);
		
		return message;
	} 
}
