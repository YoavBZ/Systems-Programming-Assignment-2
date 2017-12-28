package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.HashMap;

public class GetStudentGrades extends Action<HashMap<String, Integer>> {

	public GetStudentGrades() {
		setActionName(getClass().getSimpleName());
	}

	@Override
	protected void start() {
		HashMap<String, Integer> courses = new HashMap<>(((StudentPrivateState) state).getGrades());
		complete(courses);
	}
}