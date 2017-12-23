package bgu.spl.a2.sim;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SimulatorTest {

	@Parameterized.Parameters
	public static Collection parameters() {
		return Arrays.asList(new Object[1000][]);
	}

	public SimulatorTest(Object param) {
	}

	// Running the Simulator multiple times
	@Test
	public void simulatorTest() {
		Assert.assertEquals("Test failed!", 0, Simulator.main(new String[]{"input.json"}));
	}
}