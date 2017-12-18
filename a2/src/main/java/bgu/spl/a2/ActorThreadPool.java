package bgu.spl.a2;

import javafx.util.Pair;

import java.util.HashMap;
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
	private ConcurrentHashMap<String, Pair<PrivateState, ActorQueue<Action>>> actorsMap;
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
		actorsMap = new ConcurrentHashMap<>();
		threads = new Thread[nthreads];
		for (int i = 0; i < nthreads; i++) {
			final int index = i;
			threads[i] = new Thread(() -> {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						int version = versionMonitor.getVersion();
						for (Pair<PrivateState, ActorQueue<Action>> actorPair : actorsMap.values()) {
							ActorQueue<Action> queue = actorPair.getValue();
							if (queue.getLock().tryLock()) {
								System.out.println("Thread " + index + " locked queue " + queue.getActorId());
								try {
									if (!queue.isEmpty()) {
										Action action = queue.remove();
										System.out.println("Thread " + index + " started working on " + action.getActionName());
										action.handle(this, queue.getActorId(), actorPair.getKey());
									}
								} finally {
									System.out.println("Thread " + index + " unlocked queue " + queue.getActorId());
									queue.getLock().unlock();
								}
							}
						}
						System.out.println("Thread " + index + " await()");
						versionMonitor.await(version);
					} catch (InterruptedException e) {
						System.out.println("Thread " + index + " interrupted");
						break;
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
		Map<String, PrivateState> actors = new HashMap<>();
		for (Map.Entry<String, Pair<PrivateState, ActorQueue<Action>>> actor : actorsMap.entrySet()) {
			actors.put(actor.getKey(), actor.getValue().getKey());
		}
		return actors;
	}

	/**
	 * getter for actor's private state
	 *
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId) {
		try {
			return actorsMap.get(actorId).getKey();
		} catch (NullPointerException e) {
			return null;
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
		System.out.println("Submitted action " + action.getActionName() + " to " + actorId);
		Pair<PrivateState, ActorQueue<Action>> queuePair = actorsMap.get(actorId);
		if (queuePair == null) {
			queuePair = new Pair<>(actorState, new ActorQueue<Action>(actorId));
			actorsMap.put(actorId, queuePair);
		}
		queuePair.getValue().add(action);
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
		}
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (Thread thread : threads)
			thread.start();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Pair<PrivateState, ActorQueue<Action>> pair : actorsMap.values()) {
			str.append(pair.getKey()).append("\n");
		}
		return str.toString();
	}
}
