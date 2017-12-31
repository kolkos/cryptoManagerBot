package nl.kolkos.cryptoManagerBot.application;

import java.io.IOException;
import java.util.Properties;
import java.util.Timer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;
import org.telegram.telegrambots.generics.LongPollingBot;

import nl.kolkos.cryptoManagerBot.bots.CryptoManagerBot;
import nl.kolkos.cryptoManagerBot.services.ConfigService;

public class Application {
	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
		
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
	        
	        
//	        Timer timer = new Timer();
//	        timer.schedule(new AutomaticCommandHandler(), 0, 10000);
	        
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
