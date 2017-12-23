package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class OpenPlaceInCourse extends Action<Boolean> {

	private int spaceToAdd;

	public OpenPlaceInCourse(int spaceToAdd) {
		setActionName("Open Place In Course");
		this.spaceToAdd = spaceToAdd;
	}

	@Override
	protected void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		((CoursePrivateState) state).setAvailableSpots(((CoursePrivateState) state).getAvailableSpots() + spaceToAdd);
		complete(true);
	}
}
