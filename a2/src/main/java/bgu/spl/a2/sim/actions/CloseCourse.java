package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.Collections;
import java.util.List;

public class CloseCourse extends Action<Boolean> {

	private String courseName;

	public CloseCourse(String courseName) {
		setActionName("Close Course");
		this.courseName = courseName;
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");

		List<String> courseList = ((DepartmentPrivateState) state).getCourseList();
		if (courseList.contains(courseName)) {
			Action<Boolean> unregisterAll = new UnregisterAll();
			sendMessage(unregisterAll, courseName, new CoursePrivateState());
			then(Collections.singletonList(unregisterAll), () -> {
				if (unregisterAll.getResult().get()) {
					courseList.remove(courseName);
					complete(true);
				} else {
					complete(false);

				}
			});
		} else {
			complete(false);
		}
	}
}
