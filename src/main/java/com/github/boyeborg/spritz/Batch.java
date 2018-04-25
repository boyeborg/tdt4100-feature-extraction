package com.github.boyeborg.spritz;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A batch holds multiple collectors, and are used to group the collection of events.
 */
public class Batch<T> {

	private List<ICollector<T>> collectors;
	private String name;

	/**
	 * Creates a new batch.
	 * 
	 * @param collectorFactory A factory containing the generators to generate the collectors to use
	 *     in the batch.
	 * @param name The name of the batch
	 */
	public Batch(CollectorFactory<T> collectorFactory, String name) {
		this.name = name;
		collectors = new ArrayList<>();

		collectorFactory.get().forEach(collectors::add);
	}

	/**
	 * Adds an event to all the collectors within the {@code Batch}.
	 * 
	 * @param event The event to add to all collectors.
	 */
	public void addEvent(T event) {
		collectors.forEach(c -> c.addEvent(event));
	}

	public void process() {
		collectors.forEach(ICollector<T>::process);
	}

	/**
	 * Returns the results from the collectors.
	 * 
	 * @return An array of the results form the collectors.
	 */
	public String[] getResult() {
		return collectors.stream()
			.map(ICollector::getResult)
			.toArray(String[]::new);
	}

	/**
	 * Returns the result of the collector with the given slug. Null if the collector is not found.
	 * 
	 * @param slug The slug of a collector in the batch.
	 * 
	 * @return The result of the collector with the given slug. Null if the collector is not found.
	 */
	public String getResult(String slug) {
		return collectors.stream()
			.map(ICollector::getResult)
			.filter(c -> c.equals(slug))
			.findAny()
			.orElse(null);
	}

	/**
	 * Returns a list of the collectors within the batch.
	 * 
	 * @return A list of the collectors within the bach.
	 */
	public List<ICollector<T>> getCollectors() {
		return collectors;
	}

	/**
	 * Returns the name of the Batch.
	 * 
	 * @return The name of the batch
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		String results = collectors.stream()
				.map(ICollector::getResult)
				.collect(Collectors.joining(","));
		return name + "," + results;
	}
}
