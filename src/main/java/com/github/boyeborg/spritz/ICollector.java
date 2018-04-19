package com.github.boyeborg.spritz;

/**
 * Collector that collects data from events for feature extraction. Often combined with other
 * collectors in batches.
 */
public interface ICollector<T> {

	/**
	 * Processing method executed after all evets had been added. Usually used to calcualte values
	 * based on the results of other collector objects of the same class (from other batches), for
	 * example normalization factors.
	 */
	public void process();

	/**
	 * Adds an event to the collector.
	 */
	public void addEvent(T event);

	/**
	 * Returns the result of the collector.
	 * 
	 * @see #process()
	 */
	public String getResult();
}
