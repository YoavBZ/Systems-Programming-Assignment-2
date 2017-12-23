package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.Collections;
import java.util.List;

public class OpenCourse extends Action<Boolean> {

	private String courseName;
	private int availableSpots;
	private List<String> prequisites;

	public OpenCourse(String courseName, int availableSpots, List<String> prequisites) {
		setActionName("Open Course");
		this.courseName = courseName;
		this.availableSpots = availableSpots;
		this.prequisites = prequisites;
	}

	@Override
	protected void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		List<String> courses = ((DepartmentPrivateState) state).getCourseList();
		if (!courses.contains(courseName)) {
			System.out.println("Creating course " + courseName);
			Action<Boolean> initCourse = new InitCourse(courseName, availableSpots, prequisites);
			sendMessage(initCourse, courseName, new CoursePrivateState());
			then(Collections.singletonList(initCourse), () -> {
				courses.add(courseName);
				complete(true);
			});
		} else {
			System.out.println("Course " + courseName + " has already been created");
			then(Collections.emptyList(), () -> {
				complete(false);
				System.out.println("open " + courseName + " completed");
			});
		}
	}
}
