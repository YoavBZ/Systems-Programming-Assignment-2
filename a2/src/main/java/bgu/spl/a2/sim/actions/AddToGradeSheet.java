package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class AddToGradeSheet extends Action<Boolean> {

	private int grade;
	private String courseName;

	public AddToGradeSheet(String courseName, int grade) {
		setActionName(getClass().getSimpleName());
		this.grade = grade;
		this.courseName = courseName;

	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		((StudentPrivateState) state).getGrades().put(courseName, grade);
		complete(true);
	}
}
