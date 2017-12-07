package bgu.spl.a2;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

	private VersionMonitor versionMonitor;
	private HashMap<String, ConcurrentLinkedQueue<Action>> actorsQueue;

	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 * <p>
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads the number of threads that should be started by this thread
	 *                 pool
	 */
	public ActorThreadPool(int nthreads) {
		versionMonitor = new VersionMonitor();
		actorsQueue = new HashMap<>();
	}

	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action     the action to execute
	 * @param actorId    corresponding actor's id
	 * @param actorState actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		actorsQueue.get(actorId).add(action);
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 * <p>
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {

	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {

	}

}
