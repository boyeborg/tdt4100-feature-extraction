package com.github.boyeborg.spritz;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A factory for creating collectors.
 */
public class CollectorFactory<T> {

	private List<Supplier<ICollector<T>>> generators;

	public CollectorFactory() {
		generators = new ArrayList<>();
	}

	/**
	 * Adds a new collector generator to the factory.
	 * 
	 * @param generator A generator that generates a new collector.
	 */
	public void add(Supplier<ICollector<T>> generator) {
		generators.add(generator);
	}

	/**
	 * Returns a list of new collector objects generated from the generators added to the factory.
	 * 
	 * @return A list of new collector objects.
	 */
	public List<ICollector<T>> get() {
		List<ICollector<T>> collectors = new ArrayList<>();

		generators.forEach(g -> collectors.add(g.get()));

		return collectors;
	}

	/**
	 * Returns the number of generators in the factory.
	 * 
	 * @return The number of generators in the factory.
	 */
	public int size() {
		return generators.size();
	}

}
