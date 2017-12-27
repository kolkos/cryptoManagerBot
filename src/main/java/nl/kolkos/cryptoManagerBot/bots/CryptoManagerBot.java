package nl.kolkos.cryptoManagerBot.bots;

import com.google.common.annotations.VisibleForTesting;
import org.glassfish.hk2.api.Visibility;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Map;
import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class CryptoManagerBot extends AbilityBot {
	public CryptoManagerBot(String token, String username) {
		super(token, username);
	}

	@Override
	public int creatorId() {
		return 204878733;
	}

	public Ability sayHelloWorld() {
		return Ability.builder().name("hello").info("says hello world!").locality(ALL).privacy(PUBLIC)
				.action(ctx -> silent.send("Hello world!", ctx.chatId())).build();
	}

	/**
	 * This is very important to override for {@link ExampleBot#sayNiceToPhoto()}.
	 * By default, any update that does not have a message will not pass through
	 * abilities. To customize that, you can just override this global flag and make
	 * it return true at every update. This way, the ability flags will be the only
	 * ones responsible for checking the update's validity.
	 */
	@Override
	public boolean checkGlobalFlags(Update update) {
		return true;
	}

	public Ability sayNiceToPhoto() {
		return Ability
				.builder()
				.name(DEFAULT) // DEFAULT ability is executed if user did not specify a command -> Bot
				.flag(PHOTO)
				.privacy(PUBLIC)
				.locality(ALL)
				.input(0)
				.action(ctx -> silent.send("Daaaaang, what a nice photo!", ctx.chatId())).build();
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
