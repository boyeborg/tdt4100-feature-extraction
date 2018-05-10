package com.github.openwhale.spritz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A consumer that consumes events of type {@literal <T>}.
 * 
 */
public class EventConsumer<T> {

	private Map<String, Batch<T>> batches;
	private String currentBatchId;
	private CollectorFactory<T> collectorFactory;

	/**
	 * Constructor for the EventConsumer class.
	 * 
	 * @param collectorFactory A factory containing the generators to generate the needed collectors..
	 */
	public EventConsumer(CollectorFactory<T> collectorFactory) {
		this.collectorFactory = collectorFactory;
		this.batches = new HashMap<>();
	}

	/**
	 * Adds a new batch with the given ID, and sets it as the current batch.
	 * 
	 * @param id A unique string that identifies the batch.
	 * 
	 * @throws IllegalArgumentException If the ID is used by another batch.
	 * 
	 * @see #addEvent(Object)
	 */
	public void newBatch(String id) throws IllegalArgumentException {
		if (batches.containsKey(id)) {
			// Check that the id is unique
			String errorMsg = String.format("A batch with the id `%s` already exists.", id);
			throw new IllegalArgumentException(errorMsg);
		}
		// Generate a new batch with the given ID
		Batch<T> newBatch = new Batch<T>(collectorFactory, id);
		// Add the batch to the batch map
		batches.put(id, newBatch);
		// Update the current ID
		currentBatchId = id;
	}

	/**
	 * Adds a event to the batch with the given ID.
	 * 
	 * @param event The event to add
	 * @param id The ID of the batch to add the event to
	 * @param strict If true, an {@link IllegalArgumentException} is thrown if a batch with the given
	 *     ID does not exist. If false, a new batch with the given ID is created if it does not exist.
	 * 
	 * @throws IllegalArgumentException If the ID given is not in use by any batch and
	 *     {@literal strict} is {@code false}.
	 * 
	 * @see #newBatch(String)
	 * 
	 */
	public void addEvent(T event, String id, boolean strict) {
		// Check if the a batch with the given ID exists
		if (!batches.containsKey(id)) {
			// If in strict mode, throw an exception
			if (strict) {
				String errorMsg = String.format("No batch with id `%s` exists.", id);
				throw new IllegalArgumentException(errorMsg);
			}
			// Create a new batch when not in strict mode.
			newBatch(id);
		}
		
		batches.get(id).addEvent(event);
	}

	/**
	 * Adds a event to the batch with the given ID. Creates a new batch if a batch with the given ID
	 * does not exist.
	 * 
	 * @param event The event to add.
	 * @param id  TheID of the batch to add the event to.
	 * 
	 * @see #addEvent(Object, String, boolean)
	 */
	public void addEvent(T event, String id) {
		addEvent(event, id, false);
	}

	/**
	 * Adds a event to the current batch.
	 * 
	 * @param event The event to add.
	 * 
	 * @throws IllegalStateException If there is not current batch (i.e. there are no batches).
	 * @throws IllegalArgumentException If the there does not exist any batch with the ID of the
	 *     current batch ID.
	 * 
	 * @see #newBatch(String)
	 */
	public void addEvent(T event) throws IllegalStateException, IllegalArgumentException {
		if (currentBatchId == null) {
			throw new IllegalStateException("The current batch is `null`.");
		}
		// Add the event
		addEvent(event, currentBatchId, true);
	}

	/**
	 * Adds multiple events to the current batch.
	 * 
	 * @param events A list of the events to add
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
		batches.values().forEach(Batch::process);
	}

	/**
	 * Executes teh processing lifecy of each plugin within each batch. The batches are executed in
	 * paralell.
	 * 
	 * @throws InterruptedException One of the batches was interrupted.
	 */
	public void processMultithres() throws InterruptedException {
		List<Thread> threads = new ArrayList<>();
		
		for (Batch<T> batch : batches.values()) {
			Thread thread = new Thread(batch);
			thread.start();
			threads.add(thread);
		}

		for (Thread thread : threads) {
			thread.join();
		}
	}

	/**
	 * Returns all the results as a two dimensional matrix; one array for each batch.
	 * Each batch array contains the results from each plugin (as a string).
	 * Both the batches and results from the collectors are returned in the same order as they were
	 * added.
	 * 
	 * @return The result of each collector of each batch.
	 * 
	 */
	public String[][] getResults() {

		String[][] results = new String[batches.size()][collectorFactory.size()];

		int batchIndex = 0;

		for (Batch<T> batch : batches.values()) {
			results[batchIndex++] =  batch.getResult();
		}

		return results;
	}

	/**
	 * Returns a string with each collector result seperated by a comma, and each batch seperated by a
	 * new line.
	 * 
	 * @return A string representation of the event consumer.
	 */
	@Override
	public String toString() {
		String headers = Stream.concat(
				Stream.of("ID"),
				collectorFactory.names().stream()
		).collect(Collectors.joining(","));
		String results = batches.values().stream()
				.map(Batch::toString)
				.collect(Collectors.joining("\n"));
		return headers + "\n" + results;
	}
}
