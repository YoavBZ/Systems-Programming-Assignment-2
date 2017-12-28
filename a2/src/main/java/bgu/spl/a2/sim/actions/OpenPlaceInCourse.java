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
		if (((CoursePrivateState) state).getAvailableSpots() != -1) {
			((CoursePrivateState) state).setAvailableSpots(((CoursePrivateState) state).getAvailableSpots() + spaceToAdd);
			complete(true);
		} else {
			complete(false);
		}
	}
}
