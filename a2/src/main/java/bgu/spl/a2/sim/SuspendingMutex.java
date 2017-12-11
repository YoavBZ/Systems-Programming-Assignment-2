package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;

/**
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * <p>
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 */
public class SuspendingMutex {

	private Lock lock;
	private ConcurrentLinkedQueue<Promise<Computer>> queue;
	private Computer computer;

	/**
	 * Constructor
	 *
	 * @param computer
	 */
	public SuspendingMutex(Computer computer) {
		this.computer = computer;
	}

	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down() {
		Promise<Computer> promise = new Promise<>();
		if (lock.tryLock()) {
			return null;
		} else
			queue.add(promise);
		return promise;
	}

	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up() {
		for (Promise<Computer> promise : queue)
			promise.resolve(this.computer);
		queue.clear();
		lock.unlock();
	}
}
