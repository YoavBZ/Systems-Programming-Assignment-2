package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.List;

public class OpenNewCourse extends Action<Boolean> {

	private String courseName;
	private int availableSpots;
	private List<String> prequisites;

	public OpenNewCourse(String courseName, int availableSpots, List<String> prequisites, String department) {
		this.courseName = courseName;
		this.availableSpots = availableSpots;
		this.prequisites = prequisites;
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		List<String> courses = ((DepartmentPrivateState) state).getCourseList();
		if (!courses.contains(courseName)) {
			System.out.println("Creating course " + courseName);
			Action<Boolean> initCourse = new InitCourse(courseName, availableSpots, prequisites);
			requiredActions.add(initCourse);
			sendMessage(initCourse, courseName, new CoursePrivateState());
			continuation = () -> {
				courses.add(courseName);
				complete(true);
			};
		} else {
			System.out.println("Course " + courseName + " has already been created");
			continuation = () -> complete(false);
		}
	}
}
