package com.github.boyeborg.spritz;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A consumer that consumes events of type {@literal <T>}.
 * 
 */
public class EventConsumer<T> {

	private List<Batch<T>> batches;
	private Batch<T> currentBatch;
	private CollectorFactory<T> collectorFactory;

	/**
	 * Constructor for the EventConsumer class.
	 * 
	 * @param collectorFactory A factory containing the generators to generate the needed collectors..
	 */
	public EventConsumer(CollectorFactory<T> collectorFactory) {
		this.collectorFactory = collectorFactory;
		this.batches = new ArrayList<>();

		// Add first batch
		newBatch();
	}

	/**
	 * Adds a new batch and sets it as the current batch.
	 * 
	 * @see #addEvent(Object)
	 */
	public void newBatch() {
		currentBatch = new Batch<T>(collectorFactory);
		batches.add(currentBatch);
	}

	/**
	 * Adds a event to the current batch.
	 * 
	 * @see #newBatch()
	 */
	public void addEvent(T event) {
		currentBatch.addEvent(event);
	}

	/**
	 * Adds multiple events to the current batch.
	 * 
	 * @see #newBatch()
	 */
	public void addEvents(T[] events) {
		for (T event : events) {
			addEvent(event);
		}
	}

	/**
	 * Executes the processing lifecycle method of each plugin within each batch.
	 */
	public void process() {
		batches.forEach(Batch::process);
	}

	/**
	 * Returns all the results as a two dimensional matrix; one array for each batch.
	 * Each batch array contains the results from each plugin (as a string).
	 * Both the batches and results from the collectors are returned in the same order as they were
	 * added.
	 * 
	 * @return The result of each batch after calling {@link #calculateResult()}.
	 * 
	 */
	public String[][] getResults() {

		String[][] results = new String[batches.size()][collectorFactory.size()];

		for (int batchIndex = 0; batchIndex < batches.size(); batchIndex++) {
			results[batchIndex] =  batches.get(batchIndex).getResult();
		}

		return results;
	}

	/**
	 * Returns a string whit each collector result seperated by a comma, and each batch seperated by a
	 * new line.
	 * 
	 * @return A string representation of the event consumer.
	 */
	@Override
	public String toString() {
		return batches.stream().map(Batch::toString).collect(Collectors.joining("\n"));
	}
}
