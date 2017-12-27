package nl.kolkos.cryptoManagerBot.application;

import java.io.IOException;
import java.util.Properties;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import nl.kolkos.cryptoManagerBot.bots.CryptoManagerBot;
import nl.kolkos.cryptoManagerBot.services.ConfigService;

public class Application {
	public static void main(String[] args) {
        // Initializes dependencies necessary for the base bot - Guice
        ApiContextInitializer.init();

        // Create the TelegramBotsApi object to register your bots
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
        	// load the config
	    		ConfigService configService = new ConfigService();
	    		Properties properties = configService.loadConfig();
	    		
	    		String token = properties.getProperty("telegram.bot.token");
	    		String username = properties.getProperty("telegram.bot.username");  
        	
	        	// Register your newly created AbilityBot
	        botsApi.registerBot(new CryptoManagerBot(token, username));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
