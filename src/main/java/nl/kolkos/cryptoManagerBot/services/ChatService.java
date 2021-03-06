package nl.kolkos.cryptoManagerBot.services;

import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.repositories.ChatRepository;

public class ChatService {
	private ChatRepository chatRepository = new ChatRepository();
	
	public void saveChat(Chat chat) throws Exception {
		chatRepository.saveChat(chat);
	}
	
	public boolean checkIfChatIsRegistered(long telegramChatId) {
		boolean chatExists = false;
		try {
			chatExists = chatRepository.checkIfChatExists(telegramChatId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chatExists;
	}
	
	public boolean checkIfChatHasApiKey(Chat chat) {
		boolean botHasApi = false;
		
		if(chat.getApiKey().length() > 0) {
			botHasApi = true;
		}
		
		return botHasApi;
	}
	
	public Chat findChatByTelegramChatId(long telegramChatId) throws Exception{
		return chatRepository.findChatByTelegramChatId(telegramChatId);
	}
}
