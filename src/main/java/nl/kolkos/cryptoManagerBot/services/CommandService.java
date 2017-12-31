package nl.kolkos.cryptoManagerBot.services;

import java.util.List;

import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.repositories.CommandRepository;

public class CommandService {
	CommandRepository commandRepository = new CommandRepository();
	
	public void saveCommand(Command command) throws Exception {
		commandRepository.saveCommand(command);
	}
	
	public List<Command> getUnhandledCommands() throws Exception{
		return commandRepository.getUnhandledCommands();
	}
	
	public void updateCommand(Command command) throws Exception {
		commandRepository.updateCommand(command);
	}
	
}
