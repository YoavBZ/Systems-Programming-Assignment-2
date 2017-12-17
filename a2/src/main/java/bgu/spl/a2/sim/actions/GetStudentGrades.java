package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.HashMap;

public class GetStudentGrades extends Action<HashMap<String, Integer>> {

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		HashMap<String, Integer> courses = ((StudentPrivateState) state).getGrades();
		System.out.println("Fetched course");
		complete(courses);
	}
}