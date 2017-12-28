package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.Collections;
import java.util.List;

/**
 * This action unregisters a given student from the current course (= actorId) and updates the course spots
 */
public class Unregister extends Action<Boolean> {

	private String studentName;

	public Unregister(String studentName) {
		setActionName("Unregister");
		this.studentName = studentName;
	}

	@Override
	protected void start() {
		if (((CoursePrivateState) state).getAvailableSpots() != -1) {
			List<String> registered = ((CoursePrivateState) state).getRegStudents();
			Action<Boolean> removeFromGradeSheet = new RemoveFromGradeSheet(actorId);
			Action<Boolean> dummyAction = new DummyAction();
			sendMessage(dummyAction, studentName, new StudentPrivateState());
			then(Collections.singleton(dummyAction), () -> {
				sendMessage(removeFromGradeSheet, studentName, new StudentPrivateState());
				if (registered.contains(studentName) && ((CoursePrivateState) state).getAvailableSpots() != -1) {
					registered.remove(studentName);
					((CoursePrivateState) state).incAvailable();
					((CoursePrivateState) state).decRegistered();
				}
				then(Collections.singletonList(removeFromGradeSheet), () -> {
					if (removeFromGradeSheet.getResult().get()) {
						complete(true);
					} else {
						complete(false);
					}
				});
			});
		} else {
			complete(false);
		}

	}
}
