package nl.kolkos.cryptoManagerBot.services;

import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.repositories.CommandRepository;

public class CommandService {
	CommandRepository commandRepository = new CommandRepository();
	
	public void saveCommand(Command command) throws Exception {
		commandRepository.saveCommand(command);
	}
	
}
