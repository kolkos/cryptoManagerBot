package nl.kolkos.cryptoManagerBot.bots;

import com.google.common.annotations.VisibleForTesting;

import nl.kolkos.cryptoManagerBot.objects.CallbackQuery;
import nl.kolkos.cryptoManagerBot.objects.Chat;
import nl.kolkos.cryptoManagerBot.objects.Command;
import nl.kolkos.cryptoManagerBot.routers.CallbackQueryRouter;
import nl.kolkos.cryptoManagerBot.routers.CommandRouter;
import nl.kolkos.cryptoManagerBot.services.CallbackQueryService;
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
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Date;
import java.util.List;
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
	private CallbackQueryService callbackQueryService = new CallbackQueryService();
	
	
	private CommandRouter commandRouter = new CommandRouter();
	private CallbackQueryRouter callbackQueryRouter = new CallbackQueryRouter();
	
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
					
					// fix for group commands (automatically append the bot name)
					String fixedCommand = command.getCommand().split("@")[0];
					command.setCommand(fixedCommand);
					
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
			
			LOG.trace("Checking for unhandled callback queries...");
			try {
				List<CallbackQuery> unhandledCallbackQueries = callbackQueryService.getUnhandledCallbackQueries();
				// loop through the callback queries
				for(CallbackQuery callbackQuery : unhandledCallbackQueries) {
					LOG.info("Handling callback query '{}' send by '{}' in chat '{}'", callbackQuery.getCallbackData(), callbackQuery.getUserName(), callbackQuery.getChatId());
					
					EditMessageText editMessageText = callbackQueryRouter.callbackDataForwarder(callbackQuery);
					
					// send the message
					try {
						this.execute(editMessageText);
					} catch (TelegramApiException e) {
						String errorMessage = String.format("%s: Error sending message, '%s'. You clicked multiple times didn't you?", new Date(), e.getMessage());
						editMessageText.setText(errorMessage);
						this.execute(editMessageText);
					}
					
					
					// update this callback query
					callbackQuery.setHandled(1);
					callbackQueryService.updateCallbackQuery(callbackQuery);
				}
			} catch (Exception e) {
				LOG.fatal("Error handling unhandled callback queries: '{}'", e);
				
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
				.info("get the total value for the portfolio.")
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
							LOG.fatal("Error running /portfolio: {}", e);
						}
						// ok done, the scheduled task will handle the command
					}
				)
				.build();
	}
	
	public Ability walletCommand() {
		return Ability.builder()
				.name("wallet")
				.info("get the total value for one of the wallets.")
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
							LOG.fatal("Error running /portfolio: {}", e);
						}
						// ok done, the scheduled task will handle the command
					}
				)
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
				.flag(CALLBACK_QUERY)
				.action(ctx -> {System.out.println("callback received");})
				.reply(upd -> 
					{
						String callbackData = upd.getCallbackQuery().getData();
						long chatId = upd.getCallbackQuery().getMessage().getChatId();
						int msgId = upd.getCallbackQuery().getMessage().getMessageId();
						String user = upd.getCallbackQuery().getFrom().getUserName();
						
						LOG.info("Received callback query. Data='{}', chatId='{}', msgId='{}', user='{}'", callbackData, chatId, msgId, user);
						
						// register the callback query
						CallbackQuery callbackQuery = new CallbackQuery();
						callbackQuery.setChatId(chatId);
						callbackQuery.setMsgId(msgId);
						callbackQuery.setUserName(user);
						callbackQuery.setCallbackData(callbackData);
						
						try {
							callbackQueryService.saveCallbackQuery(callbackQuery);
						} catch (Exception e) {
							LOG.fatal("Error handling callback query: '{}'", e);
						}
						
					},
					CALLBACK_QUERY)
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
