package bgu.spl.a2;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T> the result type, <boolean> resolved - initialized ;
 */
public class Promise<T> {

	private T result = null;
	private List<callback> callbackList = new ArrayList<>();

	/**
	 *
	 * @return the resolved value if such exists (i.e., if this object has been
	 *         {@link #resolve(java.lang.Object)}ed
	 * @throws IllegalStateException
	 *             in the case where this method is called and this object is
	 *             not yet resolved
	 */
	public T get() throws IllegalStateException {
		if (!isResolved())
			throw new IllegalStateException("Promise result is null");
		return result;
	}

	/**
	 * @return true if this object has been resolved - i.e., if the method
	 * {@link #resolve(java.lang.Object)} has been called on this object
	 * before.
	 */
	public boolean isResolved() {
		return result != null;
	}

	/**
	 * resolve this promise object - from now on, any call to the method
	 * {@link #get()} should return the given result
	 * <p>
	 * Any callbacks that were registered to be notified when this object is
	 * resolved via the {@link #subscribe(callback)} method should
	 * be executed before this method returns
	 *
	 * synchronized since it can be in the middle of a subscription
	 *
	 * @param value - the result to resolve this promise object with
	 * @throws IllegalStateException in the case where this object is already resolved
	 */
	public synchronized void resolve(T value) throws IllegalStateException {
		if (!isResolved()) {
			result = value;
			for (callback c : callbackList) {
				c.call();
			}
			callbackList.clear();
		} else
			throw new IllegalStateException("Promise is already resolved");
	}

	/**
	 * add a callback to be called when this object is resolved. If while
	 * calling this method the object is already resolved - the callback should
	 * be called immediately
	 * <p>
	 * Note that in any case, the given callback should never get called more
	 * than once, in addition, in order to avoid memory leaks - once the
	 * callback got called, this object should not hold its reference any
	 * longer.
	 *
	 * synchronized since it can be in the middle of a resolving
	 *
	 * @param callback the callback to be called when the promise object is resolved
	 */
	public synchronized void subscribe(callback callback) {
		if (isResolved())
			callback.call();
		else
			callbackList.add(callback);
	}
}
