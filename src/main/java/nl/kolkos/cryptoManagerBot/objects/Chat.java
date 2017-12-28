package nl.kolkos.cryptoManagerBot.objects;

public class Chat {
	private long telegramChatId;
	private String chatName;
	private String apiKey;
	

	public long getTelegramChatId() {
		return telegramChatId;
	}
	public void setTelegramChatId(long telegramChatId) {
		this.telegramChatId = telegramChatId;
	}
	public String getChatName() {
		return chatName;
	}
	public void setChatName(String chatName) {
		this.chatName = chatName;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
}
