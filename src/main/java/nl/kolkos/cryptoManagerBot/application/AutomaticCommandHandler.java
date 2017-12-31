package nl.kolkos.cryptoManagerBot.application;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import nl.kolkos.cryptoManagerBot.bots.CryptoManagerBot;
import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.routers.CommandRouter;
import nl.kolkos.cryptoManagerBot.services.CommandService;
import nl.kolkos.cryptoManagerBot.services.ConfigService;


public class AutomaticCommandHandler extends TimerTask{
	private static final Logger LOG = LogManager.getLogger(AutomaticCommandHandler.class);
	
	private CommandService commandService = new CommandService(); 
	private CommandRouter commandRouter = new CommandRouter();
	
	private String token;
	private String username;
	
	public void run() {
		LOG.trace("Checking for unhandled commands...");
		// get the unhandled commands
		try {
			this.loadConfig();
			
			List<Command> unhandledCommands = commandService.getUnhandledCommands();
			// loop through the commands
			for(Command command : unhandledCommands) {
				// send the command object to the router
				SendMessage message = commandRouter.redirectCommand(command);
				
				// temporary create the bot
				//CryptoManagerBot bot = new CryptoManagerBot(token, username);
				//bot.execute(message);
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.fatal("Error getting unhandled commands: '{}'", e);
		}
		
	}
	
	private void loadConfig() throws IOException {
		// check if token and username are set
		if(this.token == null || this.username == null) {
			LOG.trace("Load Telegram config for Automatic Tasks");
			ConfigService configService = new ConfigService();
			Properties properties = configService.loadConfig();
			
			token = properties.getProperty("telegram.bot.token");
			username = properties.getProperty("telegram.bot.username");  
		}
		
		
	}
}
