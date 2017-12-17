package bgu.spl.a2.sim;

import java.util.List;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {

	public List<SuspendingMutex> suspendingMutexes;

	public Warehouse(Computer[] computers) {
		for (Computer computer : computers) {
			suspendingMutexes.add(computer.suspendingMutex);
		}
	}
}
