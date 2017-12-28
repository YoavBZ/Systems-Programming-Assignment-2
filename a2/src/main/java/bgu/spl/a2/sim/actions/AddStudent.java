package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.List;

public class AddStudent extends Action<Boolean> {

	private String studentName;

	public AddStudent(String studentName) {
		setActionName("Add Student");
		this.studentName = studentName;
	}

	@Override
	protected void start() {
		List<String> students = ((DepartmentPrivateState) state).getStudentList();
		if (!students.contains(studentName)) {
			students.add(studentName);
			complete(true);
		} else {
			complete(false);
		}
	}
}