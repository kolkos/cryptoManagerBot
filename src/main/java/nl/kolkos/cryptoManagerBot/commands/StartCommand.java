package nl.kolkos.cryptoManagerBot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.services.ChatService;

public class StartCommand {
	private static final Logger LOG = LogManager.getLogger(StartCommand.class);
	private ChatService chatService = new ChatService();
	
	public SendMessage startCommandAdapter(Command command) {
		LOG.trace("Handling /start command");
		// create the SendMessage
		// handle the /start command
		SendMessage message = new SendMessage() 
                .setChatId(command.getChatId())
                .setText(this.registerChat(command));
		
		return message;
	} 
	
	private String registerChat(Command command) {
		// create new Chat object
		Chat chat = new Chat();
		chat.setTelegramChatId(command.getChatId());
		chat.setChatName(command.getUserName());
		chat.setApiKey("");
		
		// save the chat
		String message = "";
		try {
			chatService.saveChat(chat);
			
			message += "Thank you, the chat is now registered.";
			message += "\n\n";
			message += "Now run the /register command to register your cryptoManager API Key";

		} catch (Exception e) {
			message = String.format("Something went wrong registering your chat: \n\n %s", e.getMessage());
			LOG.fatal("Error registering chat: {}", e);
		}
		return message;
	}
}
