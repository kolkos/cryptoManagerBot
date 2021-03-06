package nl.kolkos.cryptoManagerBot.routers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import nl.kolkos.cryptoManagerBot.commands.ChartCommand;
import nl.kolkos.cryptoManagerBot.commands.CoinCommand;
import nl.kolkos.cryptoManagerBot.commands.PortfolioCommand;
import nl.kolkos.cryptoManagerBot.commands.StartCommand;
import nl.kolkos.cryptoManagerBot.commands.TestCommand;
import nl.kolkos.cryptoManagerBot.commands.WalletCommand;
import nl.kolkos.cryptoManagerBot.objects.Command;

public class CommandRouter {
	private static final Logger LOG = LogManager.getLogger(CommandRouter.class);
	
	private CoinCommand coinCommand = new CoinCommand();
	private StartCommand startCommand = new StartCommand();
	private TestCommand testCommand = new TestCommand();
	private ChartCommand chartCommand = new ChartCommand();
	private PortfolioCommand portfolioCommand = new PortfolioCommand();
	private WalletCommand walletCommand = new WalletCommand();
	
	public SendMessage redirectCommand(Command command) {
		SendMessage message = new SendMessage() 
                .setChatId(command.getChatId())
                .setText("Command could not be handled");
		
		// get the first part of the command, to see which command it is
		String tempCommand = command.getCommand().split(" ")[0];
		
		LOG.trace("Routing '{}' command", tempCommand);
		
		switch (tempCommand) {
		case "/coin":
			// handle the coin command
			message = coinCommand.coinCommandAdapter(command);
			break;
		case "/start":
			// handle the start command
			message = startCommand.startCommandAdapter(command);
			break;
		case "/test":
			message = testCommand.testCommandAdapter(command);
			break;
		case "/chart":
			message = chartCommand.chartCommandAdapter(command);
			break;
		case "/portfolio":
			message = portfolioCommand.generatePortfolioMenu(command);
			break;
		case "/wallet":
			message = walletCommand.generateWalletMenu(command);
			break;	
		default:
			break;
		}
		
		return message;
	}
}
