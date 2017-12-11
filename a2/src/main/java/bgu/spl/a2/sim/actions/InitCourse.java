package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.List;

public class InitCourse extends Action<Boolean> {

	private String courseName;
	private int availableSpots;
	private List<String> prequisites;

	public InitCourse(String courseName, int availableSpots, List<String> prequisites) {
		this.courseName = courseName;
		this.availableSpots = availableSpots;
		this.prequisites = prequisites;
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		((CoursePrivateState) state).setAvailableSpots(availableSpots);
		((CoursePrivateState) state).setPrequisites(prequisites);
		// Add course to department action
		System.out.println("Initiated course " + courseName);
		complete(true);
	}
}