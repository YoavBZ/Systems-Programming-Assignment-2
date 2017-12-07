package bgu.spl.a2;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This class represents an actor queue
 * It contains a Lock that helps us work on this queue with only one thread at any time
 *
 * @param <T> Represents that queue members type
 */
public class ActorQueue<T> extends ConcurrentLinkedQueue<T> {

	private String actorId;
	private Lock lock = new ReentrantLock();

	public ActorQueue(String actorId) {
		this.actorId = actorId;
	}

	public Lock getLock() {
		return lock;
	}

	public String getActorId() {
		return actorId;
	}
}
