package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.Collections;
import java.util.List;

public class AddStudent extends Action<Boolean> {

	private String studentName;

	public AddStudent(String studentName) {
		setActionName("Add Student");
		this.studentName = studentName;
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		List<String> students = ((DepartmentPrivateState) state).getStudentList();
		if (!students.contains(studentName)) {
			Action<Boolean> initStudent = new InitStudent();
			sendMessage(initStudent, studentName, new StudentPrivateState());
			then(Collections.singletonList(initStudent), () -> {
				System.out.println("Added student " + studentName);
				students.add(studentName);
				complete(true);
			});
		} else {
			System.out.println("Student " + studentName + " has already been added");
			complete(false);
		}
	}
}