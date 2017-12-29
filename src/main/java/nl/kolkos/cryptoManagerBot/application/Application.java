package nl.kolkos.cryptoManagerBot.application;

import java.io.IOException;
import java.util.Properties;

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

import nl.kolkos.cryptoManagerBot.bots.CryptoManagerBot;
import nl.kolkos.cryptoManagerBot.services.ConfigService;

public class Application {
	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
		// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };
 
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
 
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		
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
