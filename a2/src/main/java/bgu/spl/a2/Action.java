package bgu.spl.a2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {

	protected String actorId;
	protected ActorThreadPool threadPool;
	protected PrivateState state;
	private String actionName;
	private AtomicInteger completedActions = new AtomicInteger(0);
	private Promise<R> promise = new Promise<>();
	private boolean alreadyStarted = false;
	protected List<Action<?>> requiredActions = new ArrayList<>();
	protected callback continuation;

	/**
	 * start handling the action - note that this method is protected, a thread
	 * cannot call it directly.
	 *
	 * This method will define the continuation callback
	 */
	protected abstract void start();

	/**
	 * start/continue handling the action
	 * <p>
	 * this method should be called in order to start this action
	 * or continue its execution in the case where it has been already started.
	 * <p>
	 * IMPORTANT: this method is package protected, i.e., only classes inside
	 * the same package can access it - you should *not* change it to
	 * public/private/protected
	 */
	/*package*/
	final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
		System.out.println("#### " + actionName + ": handle()");
		threadPool = pool;
		state = actorState;
		this.actorId=actorId;
		if (!alreadyStarted) {
			alreadyStarted = true;
			start();
			then(requiredActions, () -> {
				if (completedActions.incrementAndGet() == requiredActions.size())
					threadPool.submit(this, actorId, actorState);
			});
		} else {
			// All required action have been executed
			System.out.println("Initiate continuation of " + actionName + " in " + actorId);
			continuation.call();
		}
	}

	/**
	 * add a callback to be executed once *all* the given actions results are
	 * resolved
	 * <p>
	 * Implementors note: make sure that the callback is running only once when
	 * all the given actions completed.
	 *
	 * @param actions  required actions
	 * @param callback the callback to execute once all the results are resolved
	 */
	protected final void then(Collection<? extends Action<?>> actions, callback callback) {
		System.out.println("#### " + actionName + ": then()");
		for (Action action : actions) {
			action.promise.subscribe(callback);
		}
	}

	/**
	 * resolve the internal result - should be called by the action derivative
	 * once it is done.
	 *
	 * @param result - the action calculated result
	 */
	protected final void complete(R result) {
		System.out.println("#### " + actionName + ": complete()");
		promise.resolve(result);
		state.addRecord(getActionName());
	}

	/**
	 * @return action's promise (result)
	 */
	public final Promise<R> getResult() {
		return promise;
	}

	/**
	 * send an action to an other actor
	 *
	 * @param action     the action
	 * @param actorId    actor's id
	 * @param actorState actor's private state (actor's information)
	 * @return promise that will hold the result of the sent action
	 */
	public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState) {
		System.out.println("#### " + actionName + ": sendMessage()");
		threadPool.submit(action, actorId, actorState);
		return action.getResult();
	}

	/**
	 * set action's name
	 *
	 * @param actionName action's name
	 */
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	/**
	 * @return action's name
	 */
	public String getActionName() {
		return actionName;
	}
}
