package bgu.spl.a2.sim.actions;

import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.Action;

public class RemoveFromGradeSheet extends Action<Boolean> {
	private String courseName;

	public RemoveFromGradeSheet(String courseName) {
		setActionName(getClass().getName());
		this.courseName = courseName;
	}

	public void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		state.addRecord(getActionName());
		((StudentPrivateState) state).getGrades().remove(courseName);
		complete(true);
	}
}
