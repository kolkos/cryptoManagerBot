package nl.kolkos.cryptoManagerBot.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import nl.kolkos.cryptoManagerBot.controllers.MySQLController;
import nl.kolkos.cryptoManagerBot.objects.MenuItem;

public class MenuItemRepository {
	public MenuItemRepository() {
		try {
			this.registerMenuItems();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// register the menu items here
	private List<MenuItem> getMenuItems() {
		List<MenuItem> menuItems = new ArrayList<>();
		
		menuItems.add(new MenuItem("/chart", "Portfolio chart", "command=generatePortfolioMenu"));
		menuItems.add(new MenuItem("/chart", "Wallet chart", "command=getWallets"));
		
		return menuItems;
	}
	
	private void registerMenuItems() throws Exception {
		// create the table
		this.createMenuItemTable();
		
		// empty the table
		this.emptyMenuItemTable();
		
		// get the menu items
		List<MenuItem> menuItems = this.getMenuItems();
		
		// loop through the menu items
		for(MenuItem menuItem : menuItems) {
			this.saveMenuItem(menuItem);
		}
	}	
	
	private void createMenuItemTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS menu_item ("
				+ 	"id bigint(11) NOT NULL AUTO_INCREMENT,"
				+ 	"command varchar(255) NOT NULL,"
				+ 	"text varchar(255) NOT NULL,"
				+ 	"callback_data varchar(255) NOT NULL,"
				+ 	"PRIMARY KEY (id)"
				+ ");";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	private void emptyMenuItemTable() throws Exception {
		String query = "TRUNCATE TABLE menu_item;";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	private void saveMenuItem(MenuItem menuItem) throws Exception {
		String query = "INSERT INTO menu_item (command, text, callback_data) VALUES (?, ?, ?);";
		Object[] parameters = new Object[] {
				menuItem.getCommand(),
				menuItem.getText(),
				menuItem.getCallbackData()
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	
	public List<MenuItem> getMenuItemsForCommand(String command){
		List<MenuItem> menuItems = new ArrayList<>();
		
		// get all the menu items for this command
		String query = "SELECT * FROM menu_item WHERE command = ?";
		Object[] parameters = new Object[] {
				command
		};
		MySQLController mysql = new MySQLController();
		try {
			mysql.executeSelectQuery(query, parameters);
			ResultSet resultSet = mysql.getResultSet();
			
			// loop through the results
			while(resultSet.next()) {
				// get the required fields
				String text = resultSet.getString("text");
				String callbackData = resultSet.getString("callback_data");
				
				// create a new menu item object
				MenuItem menuItem = new MenuItem(command, text, callbackData);
				
				// add it to the list
				menuItems.add(menuItem);
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mysql.close();
		
		
		return menuItems;
	}
	
	
	
}
