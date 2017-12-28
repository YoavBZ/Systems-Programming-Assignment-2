package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.List;

/**
 * This action initiates the course with the given parameters
 */
public class InitCourse extends Action<Boolean> {

	private int availableSpots;
	private List<String> prequisites;

	public InitCourse(int availableSpots, List<String> prequisites) {
		setActionName(getClass().getSimpleName());
		this.availableSpots = availableSpots;
		this.prequisites = prequisites;
	}

	@Override
	protected void start() {
		((CoursePrivateState) state).setAvailableSpots(availableSpots);
		((CoursePrivateState) state).setPrequisites(prequisites);
		complete(true);
	}
}