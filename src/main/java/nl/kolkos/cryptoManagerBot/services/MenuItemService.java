package nl.kolkos.cryptoManagerBot.services;

import java.util.List;

import nl.kolkos.cryptoManagerBot.objects.MenuItem;
import nl.kolkos.cryptoManagerBot.repositories.MenuItemRepository;

public class MenuItemService {
	public MenuItemRepository menuItemRepository = new MenuItemRepository();
	
	public List<MenuItem> getMenuItemsForCommand(String command){
		return menuItemRepository.getMenuItemsForCommand(command);
	}
}
