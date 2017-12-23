package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.Collections;
import java.util.List;

public class Unregister extends Action<Boolean> {

	private String studentName;

	public Unregister(String studentName) {
		setActionName("Unregister");
		this.studentName = studentName;
	}

	@Override
	protected void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		List<String> registered = ((CoursePrivateState) state).getRegStudents();
		if (registered.contains(studentName)) {
			Action<Boolean> removeFromGradeSheet = new RemoveFromGradeSheet(actorId);
			sendMessage(removeFromGradeSheet, studentName, new StudentPrivateState());
			then(Collections.singletonList(removeFromGradeSheet), () -> {
				if (removeFromGradeSheet.getResult().get()) {
					registered.remove(studentName);
					((CoursePrivateState) state).incAvailable();
					((CoursePrivateState) state).decRegistered();
					complete(true);
				} else {
					complete(false);
				}
			});
		} else {
			System.out.println("wasn't registered");
			complete(false);
		}
	}
}
