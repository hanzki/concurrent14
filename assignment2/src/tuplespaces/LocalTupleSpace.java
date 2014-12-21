package tuplespaces;

import java.util.ArrayList;
import java.util.List;

public class LocalTupleSpace implements TupleSpace {
	private List<Tuple> tuples = new ArrayList<Tuple>();

	public synchronized String[] get(String... pattern) {
		Tuple tuplePattern = new Tuple(pattern);
		try {
			while(!tuples.contains(tuplePattern)){
				wait();
			}
			int i = tuples.indexOf(tuplePattern);
			Tuple result = tuples.get(i);
			tuples.remove(i);
			return result.getItems();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Tuple get interrupted");
			throw new RuntimeException("TupleSpace get got interrupted.");
		}

	}

	public synchronized String[] read(String... pattern) {
		Tuple tuplePattern = new Tuple(pattern);
		try {
			while(!tuples.contains(tuplePattern)){
				wait();
			}
			int i = tuples.indexOf(tuplePattern);
			Tuple result = tuples.get(i);
			return result.getItems();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Tuple read interrupted");
			throw new RuntimeException("TupleSpace read got interrupted.");
		}
	}

	public synchronized void put(String... tuple) {
		Tuple tupleToSave = new Tuple(tuple);
		if(tupleToSave.getItems().length <= 0)
			throw new IllegalArgumentException("Tuple lenght has to be greater than zero.");

		for(String s : tupleToSave.getItems())
			if(s == null)
				throw new IllegalArgumentException("Tuple can't contain null elements.");

		tuples.add(tupleToSave);
		notifyAll();
	}
}
