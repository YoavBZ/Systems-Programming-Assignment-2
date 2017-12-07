package bgu.spl.a2;

import javafx.util.Pair;

import java.util.HashMap;

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
	private HashMap<String, Pair<PrivateState, ActorQueue<Action>>> actorsMap;
	private Thread[] threads;

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
		actorsMap = new HashMap<>();
		threads = new Thread[nthreads];
		for (int i = 0; i < nthreads; i++) {
			threads[i] = new Thread(() -> {
				try {
					int version = versionMonitor.getVersion();
					for (Pair<PrivateState, ActorQueue<Action>> actorPair : actorsMap.values()) {
						ActorQueue<Action> queue = actorPair.getValue();
						if (queue.getLock().tryLock()) {
							try {
								if (!queue.isEmpty()) {
									queue.remove().handle(this, queue.getActorId(), actorPair.getKey());
								}
							} finally {
								queue.getLock().unlock();
							}
						}
					}
					versionMonitor.await(version);
				} catch (InterruptedException ignored) {
				}
			});
		}
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
		Pair<PrivateState, ActorQueue<Action>> queuePair = actorsMap.get(actorId);
		if (queuePair == null) {
			queuePair = new Pair<>(actorState, new ActorQueue<Action>(actorId));
			actorsMap.put(actorId, queuePair);
		}
		queuePair.getValue().add(action);
		versionMonitor.inc();
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
		for (Thread thread : threads)
			thread.join();
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (Thread thread : threads)
			thread.start();
	}

}
