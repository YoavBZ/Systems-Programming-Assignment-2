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
	 * @param computer a computer
	 */
	public SuspendingMutex(Computer computer) {
		this.computer = computer;
		locked = false;
		queue = new ArrayDeque<>();
	}

	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediately
	 * <p>
	 * This method is synchronised in order to prevent the scenario when {@link SuspendingMutex#down()} and
	 * {@link SuspendingMutex#up()} are being called simultaneously
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
	 * <p>
	 * The method  "passes the lock key" to the next department holding the promise,
	 * by resolving the next {@link Promise} in the queue if exists. Otherwise, unlocks.
	 * <p>
	 * * This method is synchronised in order to prevent the scenario when {@link SuspendingMutex#down()} and
	 * {@link SuspendingMutex#up()} are being called simultaneously
	 */
	public synchronized void up() {
		if (!queue.isEmpty())
			queue.remove().resolve(this.computer);
		else
			locked = false;
	}
}