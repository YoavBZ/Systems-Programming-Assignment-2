package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;
import java.util.Set;

public class GetStudentCourses extends Action<Set<String>> {

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		Set<String> courses =((StudentPrivateState) state).getGrades().keySet();
		System.out.println("Fetched course");
		complete(courses);
	}
}