package bgu.spl.a2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	private ConcurrentHashMap<String, PrivateState> privateStates;
	private ConcurrentHashMap<String, ActorQueue<Action>> queues;
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
		privateStates = new ConcurrentHashMap<>();
		queues = new ConcurrentHashMap<>();
		threads = new Thread[nthreads];
		for (int i = 0; i < nthreads; i++) {
			final int index = i;
			threads[i] = new Thread(() -> {
				while (!Thread.interrupted()) {
					try {
						int version = versionMonitor.getVersion();
						for (ActorQueue<Action> queue : queues.values()) {
							boolean handledAction = false;
							if (queue.getLock().tryLock()) {
								String actorId = queue.getActorId();
								System.out.println("Thread " + index + " locked queue " + actorId);
								try {
									if (!queue.isEmpty()) {
										Action action = queue.remove();
										System.out.println("Thread " + index + " started working on " + action.getActionName());
										action.handle(this, actorId, privateStates.get(actorId));
										handledAction = true;
									}
								} finally {
									System.out.println("Thread " + index + " unlocked queue " + actorId);
									queue.getLock().unlock();
									if (handledAction)
										versionMonitor.inc();
								}
							}
							if (Thread.interrupted())
								return;
						}
						System.out.println("Thread " + index + " await()");
						versionMonitor.await(version);
					} catch (InterruptedException e) {
						System.out.println("Thread " + index + " interrupted");
						return;
					}
				}
			});
		}
	}

	/**
	 * getter for actors
	 *
	 * @return actors
	 */
	public Map<String, PrivateState> getActors() {
		return privateStates;
	}

	/**
	 * getter for actor's private state
	 *
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId) {
		try {
			return privateStates.get(actorId);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 * <p>
	 * This method is synchronized to prevent creating the same ActorQueue multiple times simultaneously
	 *
	 * @param action     the action to execute
	 * @param actorId    corresponding actor's id
	 * @param actorState actor's private state (actor's information)
	 */
	public synchronized void submit(Action<?> action, String actorId, PrivateState actorState) {
		System.out.println("Submitted action " + action.getActionName() + " to " + actorId);
		ActorQueue<Action> queue = queues.get(actorId);
		if (queue == null) {
			queue = new ActorQueue<>(actorId);
			privateStates.put(actorId, actorState);
			queues.put(actorId, queue);
		}
		queue.add(action);
		System.out.println("Version incremented!");
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
		for (Thread thread : threads) {
			thread.interrupt();
			thread.join();
		}
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (Thread thread : threads)
			thread.start();
	}
}