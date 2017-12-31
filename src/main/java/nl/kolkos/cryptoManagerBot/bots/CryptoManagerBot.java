package nl.kolkos.cryptoManagerBot.bots;

import com.google.common.annotations.VisibleForTesting;


import nl.kolkos.cryptoManagerBot.commands.CallbackQueryCommand;
import nl.kolkos.cryptoManagerBot.commands.CoinCommand;
import nl.kolkos.cryptoManagerBot.commands.TestCommand;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.routers.CommandRouter;
import nl.kolkos.cryptoManagerBot.services.ChatService;
import nl.kolkos.cryptoManagerBot.services.CommandService;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class CryptoManagerBot extends AbilityBot {
	private static final Logger LOG = LogManager.getLogger(CryptoManagerBot.class);
	
	private ChatService chatService = new ChatService();
	private CommandService commandService = new CommandService();
	
	private TestCommand testCommand = new TestCommand();
	private CoinCommand coinCommand = new CoinCommand();
	private CallbackQueryCommand callbackQueryCommand = new CallbackQueryCommand();
	private CommandRouter commandRouter = new CommandRouter();
	
	Timer timer;
	
	public CryptoManagerBot(String token, String username) {
		super(token, username);
		
		// this will handle the commands in the database
		ScheduledExecutorService execService = Executors.newScheduledThreadPool(5);
		execService.scheduleAtFixedRate(()->{
			LOG.trace("Checking for unhandled commands...");
			try {
				List<Command> unhandledCommands = commandService.getUnhandledCommands();
				// loop through the commands
				for(Command command : unhandledCommands) {
					LOG.info("Handling command '{}' send by '{}' in chat '{}'", command.getCommand(), command.getUserName(), command.getChatId());
					
					// send the command object to the router
					SendMessage message = commandRouter.redirectCommand(command);
					
					// send the message
					this.execute(message);
					
					// now update the command (handled = 1)
					command.setCommandHandled(1);
					commandService.updateCommand(command);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOG.fatal("Error handling unhandled commands: '{}'", e);
			}
		}, 0, 1000L, TimeUnit.MILLISECONDS);
	}
	
	

	@Override
	public int creatorId() {
		return 204878733;
	}
	
	
	
	public Ability startCommand() {
		return Ability.builder()
				.name("start")
				.info("Run this command to start using this bot")
				.locality(ALL)
				.privacy(PUBLIC)
				.input(0)
				.action(ctx -> 
					{
						LOG.info("Received the command /start from chat '{}' send by '{}'", ctx.chatId(), ctx.user().username());
						
						// register this Command
						Command command = new Command();
						command.setChatId(ctx.chatId());
						command.setUserName(ctx.user().username());
						command.setCommand(ctx.update().getMessage().getText());
						try {
							commandService.saveCommand(command);
						} catch (Exception e) {
							LOG.fatal("Error running /start: {}", e);
						}
						// ok done, the scheduled task will handle the command
						
					}
				)
				.build();
	}
	
	public Ability registerApiKey() {
		String message = "Please send me the API key you wish to register for this chat.";
		return Ability.builder()
				.name("register")
				.info("register an cryptoManager API key.")
				.locality(ALL)
				.privacy(PUBLIC)
				.input(0)
				.action(ctx -> 
					{
						// register this Command
						Command command = new Command();
						command.setChatId(ctx.chatId());
						command.setUserName(ctx.user().username());
						command.setCommand(ctx.update().getMessage().getText());
						command.setCommandHandled(1);
						try {
							commandService.saveCommand(command);
							
							// check if the chat is registered yet 
							if(!chatService.checkIfChatIsRegistered(ctx.chatId())) {
								LOG.warn("/register command received from unregisterd chat '{}'", ctx.chatId());
								silent.send("Chat not registered. Please run the /start command first.", ctx.chatId());
							} else {
								silent.forceReply(message, ctx.chatId());
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							silent.send(String.format("Error handling the /register command: '%s'", e.getMessage()), ctx.chatId());
							LOG.fatal("Error running /register: {}", e);
						}
						
					}
				)
				.reply(upd -> 
					{
						LOG.info("Reply on /register received in chat {} from {}", upd.getMessage().getChatId(), upd.getMessage().getChat().getUserName());
						
						// create the chat object
						try {
							Chat chat = chatService.findChatByTelegramChatId(upd.getMessage().getChatId());
							
							// add the api key to the chat object
							String apiKey = upd.getMessage().getText();
							LOG.warn("api key received: '{}'", apiKey);
							
							chat.setApiKey(apiKey);
							
							// save the Chat object
							chatService.saveChat(chat);
													
							// Sends message
							silent.send(String.format("API Key '%s' registered", apiKey), upd.getMessage().getChatId());
							silent.send("Send the command /test to check the status of the api key", upd.getMessage().getChatId());	
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							String errorMsg = String.format("Something went wrong registering your API: \n\n %s", e.getMessage());
							silent.send(errorMsg, upd.getMessage().getChatId());
							
						}
					},
					MESSAGE,
					REPLY,
					isReplyToBot(),
					isReplyToMessage(message)
				)
				.build();
	}
	
	public Ability coinCommand() {
		return Ability.builder()
				.name("coin")
				.info("gets the last known value for all the registered coins.")
				.locality(ALL)
				.privacy(PUBLIC)
				.input(0)
				.action(ctx -> 
					{
						// register this Command
						Command command = new Command();
						command.setChatId(ctx.chatId());
						command.setUserName(ctx.user().username());
						command.setCommand(ctx.update().getMessage().getText());
						try {
							commandService.saveCommand(command);
						} catch (Exception e) {
							LOG.fatal("Error running /coin: {}", e);
						}
						// ok done, the scheduled task will handle the command
					}
				)
				.build();
	}

	public Ability portfolioCommand() {
		return Ability.builder()
				.name("portfolio")
				.info("get the value of the portfolio(s) this bot has access to.")
				.locality(ALL)
				.privacy(PUBLIC)
				.input(0)
				.action(ctx -> silent.send("TODO: Handle portfolio command", ctx.chatId()))
				.build();
	}
	
	public Ability chartCommand() {
		return Ability.builder()
				.name("chart")
				.info("create a chart")
				.locality(ALL)
				.privacy(PUBLIC)
				.input(0)
				.action(ctx -> {
					SendMessage message = callbackQueryCommand.createMenuForCommand("Which chart you wish to create?", "/chart", ctx.chatId(), 1);
	                
	                silent.execute(message);
				})
				.build();
	}
	
	public Ability sayHelloWorld() {
		return Ability.builder()
				.name("hello")
				.info("says hello world!")
				.locality(ALL)
				.privacy(PUBLIC)
				.action(ctx -> silent.send("Hello world!", ctx.chatId()))
				.build();
	}
	
	public Ability testStatus() {
		return Ability.builder()
				.name("test")
				.info("test the bot status")
				.locality(ALL)
				.privacy(PUBLIC)
				.action(ctx -> 
					{
						// register this Command
						Command command = new Command();
						command.setChatId(ctx.chatId());
						command.setUserName(ctx.user().username());
						command.setCommand(ctx.update().getMessage().getText());
						try {
							commandService.saveCommand(command);
						} catch (Exception e) {
							LOG.fatal("Error running /test: {}", e);
						}
						// scheduler will handle the command
					}
				)
				.build();
	}
	
	public Ability callbackHandler() {
		return Ability.builder()
				.name(DEFAULT)
				.info("handle callback queries")
				.locality(ALL)
				.privacy(PUBLIC)
				.action(ctx -> {System.out.println("callback received");})
				.reply(upd -> 
					{
						String callbackData = upd.getCallbackQuery().getData();
						long chatId = upd.getCallbackQuery().getMessage().getChatId();
						int msgId = upd.getCallbackQuery().getMessage().getMessageId();
						String user = upd.getCallbackQuery().getFrom().getUserName();
						
						LOG.info("Received callback query. Data='{}', chatId='{}', msgId='{}', user='{}'", callbackData, chatId, msgId, user);
						EditMessageText editMessageText = callbackQueryCommand.callbackDataForwarder(
								callbackData, 
								chatId, 
								msgId);
						
						silent.execute(editMessageText);
						
					},
					CALLBACK_QUERY)
				.build();
	}
	
	public Ability playWithMe() {
		String playMessage = "Play with me!";

		return Ability.builder()
				.name("play")
				.info("Do you want to play with me?")
				.privacy(PUBLIC)
				.locality(ALL)
				.input(0)
				.action(ctx -> silent.forceReply(playMessage, ctx.chatId()))
				// The signature of a reply is -> (Consumer<Update> action, Predicate<Update>...
				// conditions)
				// So, we first declare the action that takes an update (NOT A MESSAGECONTEXT)
				// like the action above
				// The reason of that is that a reply can be so versatile depending on the
				// message, context becomes an inefficient wrapping
				.reply(upd -> {
					// Prints to console
					System.out.println("I'm in a reply!");
					// Sends message
					silent.send("It's been nice playing with you!", upd.getMessage().getChatId());
				},
					// Now we start declaring conditions, MESSAGE is a member of the enum Flag class
					// That class contains out-of-the-box predicates for your replies!
					// MESSAGE means that the update must have a message
					// This is imported statically, Flag.MESSAGE
					MESSAGE,
					// REPLY means that the update must be a reply, Flag.REPLY
					REPLY,
					// A new predicate user-defined
					// The reply must be to the bot
					isReplyToBot(),
					// If we process similar logic in other abilities, then we have to make this
					// reply specific to this message
					// The reply is to the playMessage
					isReplyToMessage(playMessage))
				// You can add more replies by calling .reply(...)
				.build();
	}

	private Predicate<Update> isReplyToMessage(String message) {
		return upd -> {
			Message reply = upd.getMessage().getReplyToMessage();
			return reply.hasText() && reply.getText().equalsIgnoreCase(message);
		};
	}

	private Predicate<Update> isReplyToBot() {
		return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
	}

	@VisibleForTesting
	void setSender(MessageSender sender) {
		this.sender = sender;
	}
	
}
