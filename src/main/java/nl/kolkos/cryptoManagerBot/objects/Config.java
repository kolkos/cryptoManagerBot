package nl.kolkos.cryptoManagerBot.objects;

public class Config {
	/*
	 * Telegram settings
	 */
	private String botToken;
	private String botUsername;
	
	/*
	 * Database settings
	 */
	private String mysqlUrl;
	private String mysqlUser;
	private String mysqlPass;
	
	/*
	 * Getters and setters
	 */
	public String getBotToken() {
		return botToken;
	}
	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}
	public String getBotUsername() {
		return botUsername;
	}
	public void setBotUsername(String botUsername) {
		this.botUsername = botUsername;
	}
	public String getMysqlUrl() {
		return mysqlUrl;
	}
	public void setMysqlUrl(String mysqlUrl) {
		this.mysqlUrl = mysqlUrl;
	}
	public String getMysqlUser() {
		return mysqlUser;
	}
	public void setMysqlUser(String mysqlUser) {
		this.mysqlUser = mysqlUser;
	}
	public String getMysqlPass() {
		return mysqlPass;
	}
	public void setMysqlPass(String mysqlPass) {
		this.mysqlPass = mysqlPass;
	}
	
	
}
