package nl.kolkos.cryptoManagerBot.services;

import java.util.List;

import nl.kolkos.cryptoManagerBot.objects.Period;
import nl.kolkos.cryptoManagerBot.repositories.PeriodRepository;

public class PeriodService {
	private PeriodRepository periodRepository = new PeriodRepository();
	
	public List<Period> getPeriods() throws Exception{
		return periodRepository.getPeriods();
	}
}
