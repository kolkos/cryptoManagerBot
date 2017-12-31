package nl.kolkos.cryptoManagerBot.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import nl.kolkos.cryptoManagerBot.controllers.MySQLController;
import nl.kolkos.cryptoManagerBot.objects.CallbackQuery;

public class CallbackQueryRepository {
	private void createCallbackQueryTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS callback_query ("
				+ 	"id bigint(11) NOT NULL AUTO_INCREMENT,"
				+ 	"timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ 	"chat_id bigint(11) NOT NULL,"
				+ 	"msg_id int(5) NOT NULL,"
				+ 	"username varchar(255),"
				+ 	"callback_data varchar(255),"
				+ 	"handled int(1) DEFAULT 0,"
				+ 	"timestamp_handled DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
				+ 	"PRIMARY KEY (id)"
				+ ");";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public void saveCallbackQuery(CallbackQuery callbackQuery) throws Exception {
		this.createCallbackQueryTable();
		
		String query = "INSERT INTO callback_query (chat_id, msg_id, username, callback_data, handled) VALUES (?, ?, ?, ?, ?)";
		Object[] parameters = new Object[] {
				callbackQuery.getChatId(),
				callbackQuery.getMsgId(),
				callbackQuery.getUserName(),
				callbackQuery.getCallbackData(),
				callbackQuery.getHandled()
		};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public void updateCallbackQuery(CallbackQuery callbackQuery) throws Exception {
		String query = "UPDATE callback_query SET handled = ? WHERE id = ?;";
		Object[] parameters = new Object[] {
				callbackQuery.getHandled(),
				callbackQuery.getId()
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public List<CallbackQuery> getUnhandledCallbackQueries() throws Exception{
		List<CallbackQuery> unhandledCallbackQueries = new ArrayList<>();
		
		String query = "SELECT * FROM callback_query WHERE handled = 0;";
		Object[] parameters = new Object[] {};
		MySQLController mysql = new MySQLController();
		
		mysql.executeSelectQuery(query, parameters);
		ResultSet resultSet = mysql.getResultSet();
		while(resultSet.next()) {
			// create a new CallbackQuery object
			CallbackQuery callbackQuery = new CallbackQuery();
			callbackQuery.setId(resultSet.getLong("id"));
			callbackQuery.setChatId(resultSet.getLong("chat_id"));
			callbackQuery.setMsgId(resultSet.getInt("msg_id"));
			callbackQuery.setUserName(resultSet.getString("username"));
			callbackQuery.setCallbackData(resultSet.getString("callback_data"));
			
			unhandledCallbackQueries.add(callbackQuery);
		}
		
		return unhandledCallbackQueries;
	}
}
