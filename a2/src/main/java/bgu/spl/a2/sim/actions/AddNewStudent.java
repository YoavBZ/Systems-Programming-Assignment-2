package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.List;

public class AddNewStudent extends Action<Boolean> {

	private String studentName;

	public AddNewStudent(String studentName) {
		setActionName("Add Student");
		this.studentName = studentName;
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		state.addRecord(getActionName());
		List<String> students = ((DepartmentPrivateState) state).getStudentList();
		if (!students.contains(studentName)) {
			System.out.println("Added student " + studentName);
			students.add(studentName);
			complete(true);
		} else {
			System.out.println("Student " + studentName + " has already been added");
			complete(false);
		}

	}
}
