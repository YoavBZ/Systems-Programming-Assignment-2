package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * <p>
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 */
public class SuspendingMutex {

	private boolean locked;
	private Queue<Promise<Computer>> queue;
	public Computer computer;

	/**
	 * Constructor
	 *
	 * @param computer
	 */
	public SuspendingMutex(Computer computer) {
		this.computer = computer;
		locked = false;
		queue = new ArrayDeque<>();
	}

	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediately
	 *
	 * @return a promise for the requested computer
	 */
	public synchronized Promise<Computer> down() {
		Promise<Computer> newPromise = new Promise<>();
		if (!locked) {
			locked = true;
			newPromise.resolve(this.computer);
		} else
			queue.add(newPromise);
		return newPromise;
	}

	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public synchronized void up() {
		if (!queue.isEmpty())
			queue.remove().resolve(this.computer);
		else
			locked = false;
	}
}