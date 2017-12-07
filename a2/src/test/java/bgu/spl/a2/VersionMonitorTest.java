package bgu.spl.a2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class VersionMonitorTest {

	private VersionMonitor versionMonitor;

	@Before
	public void setUp() {
		versionMonitor = new VersionMonitor();
	}

	// Simple test for getVersion(), a more thorough test will be executed in inc() test
	@Test
	public void getVersion() {
		Assert.assertEquals(0, versionMonitor.getVersion());
	}

	@Test
	public void inc() {
		// Tests single incrementation
		int version = versionMonitor.getVersion();
		versionMonitor.inc();
		Assert.assertEquals(version + 1, versionMonitor.getVersion());

		// Tests multi-threaded incrementation
		version = versionMonitor.getVersion();
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		for (int i = 0; i < 10; i++) {
			threadPool.execute(() -> versionMonitor.inc());
		}
		try {
			threadPool.shutdown();
			if (!threadPool.awaitTermination(10, TimeUnit.SECONDS))
				Assert.fail("ThreadPool awaitTermination timeout!");
		} catch (InterruptedException e) {
			Assert.fail();
		}
		Assert.assertEquals(version + 10, versionMonitor.getVersion());
	}

	@Test
	public void await() {
		// Checks if the thread waits until the version is changed, and if it releases after
		AtomicBoolean waited = new AtomicBoolean(false);
		Thread thread = new Thread(() -> {
			try {
				versionMonitor.await(versionMonitor.getVersion());
				waited.set(true);
			} catch (InterruptedException e) {
				Assert.fail();
			}
		});
		thread.start();
		try {
			Thread.sleep(1000);
			Assert.assertFalse(waited.get());
			versionMonitor.inc();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Assert.fail();
		}
		Assert.assertTrue(waited.get());

		// Tests that the thread doesn't wait if wrong version is sent
		waited.set(false);
		int currentVersion = versionMonitor.getVersion();
		thread = new Thread(() -> {
			try {
				versionMonitor.await(currentVersion - 1);
				waited.set(true);
			} catch (InterruptedException e) {
				Assert.fail();
			}
		});
		thread.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Assert.fail();
		}
		Assert.assertTrue(waited.get());
	}
}