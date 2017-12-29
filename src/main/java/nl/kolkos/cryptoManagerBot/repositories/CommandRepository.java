package nl.kolkos.cryptoManagerBot.repositories;

import nl.kolkos.cryptoManagerBot.controllers.MySQLController;
import nl.kolkos.cryptoManagerBot.objects.Command;

public class CommandRepository {
	private void createCommandTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS command ("
				+ 	"id bigint(11) NOT NULL AUTO_INCREMENT,"
				+ 	"timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ 	"chat_id bigint(11) NOT NULL,"
				+ 	"username varchar(255),"
				+ 	"command varchar(255),"
				+ 	"PRIMARY KEY (id)"
				+ ");";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public void saveCommand(Command command) throws Exception {
		this.createCommandTable();
		String query = "INSERT INTO command (chat_id, username, command) VALUES (?, ?, ?);";
		Object[] parameters = new Object[] {
				command.getChatId(),
				command.getUserName(),
				command.getCommand()
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	
}
