package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * <p>
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 */
public class SuspendingMutex {

	private Lock lock;
	private Queue<Promise<Computer>> queue;
	public Computer computer;

	/**
	 * Constructor
	 *
	 * @param computer
	 */
	public SuspendingMutex(Computer computer) {
		this.computer = computer;
		lock = new ReentrantLock();
		queue = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediately
	 *
	 * @return a promise for the requested computer
	 */
	public synchronized Promise<Computer> down() {
		Promise<Computer> newPromise = new Promise<>();
		if (lock.tryLock()) {
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
			lock.unlock();
	}
}
