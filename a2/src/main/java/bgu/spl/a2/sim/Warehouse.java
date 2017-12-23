package bgu.spl.a2.sim;

import java.util.HashMap;
import java.util.Map;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {

	public Map<String, SuspendingMutex> computers;

	public Warehouse(Computer[] computers) {
		this.computers = new HashMap<>();
		for (Computer computer : computers) {
			this.computers.put(computer.computerType, new SuspendingMutex(computer));
		}
	}
}
