package nl.kolkos.cryptoManagerBot.services;

import java.util.List;

import nl.kolkos.cryptoManagerBot.objects.Interval;
import nl.kolkos.cryptoManagerBot.repositories.IntervalRepository;

public class IntervalService {
	IntervalRepository intervalRepository = new IntervalRepository();
	
	public List<Interval> getIntervalsForPeriod(String period) throws Exception{
		return intervalRepository.getIntervalsForPeriod(period);
	}
}
