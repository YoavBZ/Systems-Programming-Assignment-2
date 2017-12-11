package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

public class AddNewStudent extends Action<Boolean> {

	private String studentName;
	private long signature;

	public AddNewStudent(String studentName, long signature) {
		this.studentName = studentName;
		this.signature = signature;
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		List<String> students = ((DepartmentPrivateState) state).getStudentList();
		if (!students.contains(studentName)) {
			System.out.println("Added student " + studentName);
			Action<Boolean> initStudent = new InitStudent(studentName, signature);
			requiredActions.add(initStudent);
			sendMessage(initStudent, studentName, new StudentPrivateState());
			continuation = () -> {
				students.add(studentName);
				complete(true);
			};
		} else {
			System.out.println("Student " + studentName + " has already been added");
			continuation = () -> complete(false);
		}

	}
}
