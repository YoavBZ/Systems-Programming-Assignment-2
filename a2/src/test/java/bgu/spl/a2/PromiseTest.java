package bgu.spl.a2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PromiseTest {

	private Promise<Integer> promise;

	@Before
	public void setUp() {
		promise = new Promise<>();
	}

	@Test
	public void testGet() {
		try {
			promise.get();
		} catch (IllegalStateException e) {
			// An IllegalStateException throwing is expected
			promise.resolve(4);
			Assert.assertEquals(4, (int) promise.get());
		} catch (Exception e) {
			Assert.fail("Got an exception during test" + e.getMessage());
		}
	}

	// Tests that promise hasn't been resolved after initiating, then resolve it and check if resolved
	@Test
	public void testIsResolved() {
		try {
			Assert.assertFalse(promise.isResolved());
			promise.resolve(4);
			Assert.assertTrue(promise.isResolved());
		} catch (Exception e) {
			Assert.fail("Got an exception during test" + e.getMessage());
		}
	}

	// Tests that resolving a promise actually updates its value,
	// and that a resolved promise cannot be resolved again
	@Test
	public void testResolve() {
		int[] array = new int[]{0, 0, 0};
		try {
			promise.subscribe(() -> array[0]++);
			promise.subscribe(() -> array[1]++);
			promise.subscribe(() -> array[2]++);
			promise.resolve(4);
			promise.resolve(5);
			Assert.fail("Didn't throw an exception when resolved an already resolved Promise");
		} catch (IllegalStateException e) {
			Assert.assertEquals("Resolved promise value has been changed", 4, (int) promise.get());
			for (int i = 0; i < 3; i++) {
				Assert.assertEquals("Not all callbacks have been triggered", array[i], 1);
			}
		} catch (Exception e) {
			Assert.fail("Got an exception during test" + e.getMessage());
		}
	}

	@Test
	public void testSubscribe() {
		int[] array = new int[]{0, 0, 0};
		try {
			promise.subscribe(() -> array[0]++);
			promise.subscribe(() -> array[1]++);
			promise.subscribe(() -> array[2]++);
			for (int i = 0; i < 3; i++) {
				Assert.assertEquals("Some callback has been triggered", 0, array[i]);
			}
			promise.resolve(4);
			for (int i = 0; i < 3; i++) {
				Assert.assertEquals("Not all callbacks have been triggered", 1, array[i]);
			}
			promise.subscribe(() -> array[0]++);
			Assert.assertEquals("Some callback has been triggered", 2, array[0]);
		} catch (Exception e) {
			Assert.fail("Got an exception during test" + e.getMessage());
		}
	}
}