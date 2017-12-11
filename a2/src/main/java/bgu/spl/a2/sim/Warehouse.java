package bgu.spl.a2.sim;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {

	private SuspendingMutex[] suspendingMutexes;

	public Warehouse(Computer[] computers) {
		suspendingMutexes = new SuspendingMutex[computers.length];
		for (int i = 0; i < computers.length; i++) {
			suspendingMutexes[i] = new SuspendingMutex(computers[i]);
		}
	}
}
