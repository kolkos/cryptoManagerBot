package nl.kolkos.cryptoManagerBot.services;

import java.util.List;

import nl.kolkos.cryptoManagerBot.objects.CallbackQuery;
import nl.kolkos.cryptoManagerBot.repositories.CallbackQueryRepository;

public class CallbackQueryService {
	private CallbackQueryRepository callbackQueryRepository = new CallbackQueryRepository();
	
	public void saveCallbackQuery(CallbackQuery callbackQuery) throws Exception {
		callbackQueryRepository.saveCallbackQuery(callbackQuery);
	}
	
	public List<CallbackQuery> getUnhandledCallbackQueries() throws Exception{
		return callbackQueryRepository.getUnhandledCallbackQueries();
	}
	
	public void updateCallbackQuery(CallbackQuery callbackQuery) throws Exception {
		callbackQueryRepository.updateCallbackQuery(callbackQuery);
	}
}
