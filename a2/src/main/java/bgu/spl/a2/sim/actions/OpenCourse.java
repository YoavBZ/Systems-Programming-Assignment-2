package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.Collections;
import java.util.List;

/**
 * This action uses {@link InitCourse} action to initiate to course
 */
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
		List<String> courses = ((DepartmentPrivateState) state).getCourseList();
		if (!courses.contains(courseName)) {
			Action<Boolean> initCourse = new InitCourse(availableSpots, prequisites);
			sendMessage(initCourse, courseName, new CoursePrivateState());
			then(Collections.singletonList(initCourse), () -> {
				courses.add(courseName);
				complete(true);
			});
		} else {
			complete(false);
		}
	}
}
