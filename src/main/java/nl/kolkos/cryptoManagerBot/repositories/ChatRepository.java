package nl.kolkos.cryptoManagerBot.repositories;

import java.sql.ResultSet;

import nl.kolkos.cryptoManagerBot.controllers.MySQLController;
import nl.kolkos.cryptoManagerBot.objects.Chat;

public class ChatRepository {
	private void createChatTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS chat ("
				+ 	"telegram_chat_id bigint(11) NOT NULL,"
				+ 	"name varchar(255),"
				+ 	"api_key varchar(255),"
				+ 	"PRIMARY KEY (telegram_chat_id)"
				+ ");";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	/**
	 * Save a new chat to the database
	 * @param chat
	 * @throws Exception
	 */
	private void saveNewChat(Chat chat) throws Exception {
		String query = "INSERT INTO chat (telegram_chat_id, name, api_key) VALUES (?, ?, ?);";
		Object[] parameters = new Object[] {
				chat.getTelegramChatId(),
				chat.getChatName(),
				chat.getApiKey(),
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	/**
	 * Update a existing chat 
	 * @param chat
	 * @throws Exception
	 */
	private void updateExistingChat(Chat chat) throws Exception {
		String query = "UPDATE chat SET name = ?, api_key = ? WHERE telegram_chat_id = ?";
		Object[] parameters = new Object[] {
				chat.getChatName(),
				chat.getApiKey(),
				chat.getTelegramChatId(),
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public boolean checkIfChatExists(long telegramChatId) throws Exception {
		boolean chatExists = false;
		
		String query = "SELECT COUNT(telegram_chat_id) AS numberFound FROM chat WHERE telegram_chat_id = ?;";
		Object[] parameters = new Object[] {telegramChatId};
		
		MySQLController mysql = new MySQLController();
		mysql.executeSelectQuery(query, parameters);
		
		ResultSet resultSet = mysql.getResultSet();
		int numberFound = 0;
		while(resultSet.next()) {
			numberFound = resultSet.getInt("numberFound");
		}
		
		if(numberFound > 0) {
			chatExists = true;
		}
		mysql.close();
		return chatExists;
	}
	
	public void saveChat(Chat chat) throws Exception {
		// create the chat table if necessary
		this.createChatTable();
		
		// check if the chat exists
		if(this.checkIfChatExists(chat.getTelegramChatId())) {
			// chat exists, update the existing chat
			this.updateExistingChat(chat);
		}else {
			// chat does not exist, create a new entry
			this.saveNewChat(chat);
		}
		
	}
	
	public Chat findChatByTelegramChatId(long telegramChatId) throws Exception {
		// check if the chat exists
		if(!this.checkIfChatExists(telegramChatId)) {
			return null;
		}
		
		Chat chat = new Chat();
		
		String query = "SELECT * FROM chat WHERE telegram_chat_id = ?;";
		Object[] parameters = new Object[] {telegramChatId};
		
		MySQLController mysql = new MySQLController();
		mysql.executeSelectQuery(query, parameters);
		
		ResultSet resultSet = mysql.getResultSet();
		while(resultSet.next()) {
			chat.setTelegramChatId(resultSet.getLong("telegram_chat_id"));
			chat.setChatName(resultSet.getString("name"));
			chat.setApiKey(resultSet.getString("api_key"));
		}
		mysql.close();
		
		return chat;
	}
}
