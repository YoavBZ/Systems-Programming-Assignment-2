package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class RemoveFromGradeSheet extends Action<Boolean> {
	private String courseName;

	public RemoveFromGradeSheet(String courseName) {
		setActionName(getClass().getSimpleName());
		this.courseName = courseName;
	}

	public void start() {
		if (((StudentPrivateState) state).getGrades().remove(courseName) != null)
			complete(true);
		else
			complete(false);
	}
}
