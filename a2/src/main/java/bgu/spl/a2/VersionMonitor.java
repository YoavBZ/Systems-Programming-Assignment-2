package bgu.spl.a2;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 * <p>
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {

	private AtomicInteger version = new AtomicInteger(0);

	public int getVersion() {
		return version.get();
	}

	/**
	 * Increments version
	 * This method is synchronised since notifyAll() must be called from a synchronized block
	 * to prevent {@link IllegalMonitorStateException}
	 */
	public synchronized void inc() {
		version.incrementAndGet();
		notifyAll();
	}

	/**
	 * This method await the current thread if the current version equals to the given version
	 * This method is synchronised since wait() must be called from a synchronised block
	 * to prevent {@link IllegalMonitorStateException}
	 *
	 * @param version a version
	 * @throws InterruptedException thrown when the current thread is intercepted while waiting
	 */
	public synchronized void await(int version) throws InterruptedException {
		while (version == this.version.get()) {
			wait();
		}
	}
}