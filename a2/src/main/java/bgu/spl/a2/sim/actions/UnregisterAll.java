package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * This action unregisters a course (= actorId) for each student in the registered students list,
 */
public class UnregisterAll extends Action<Boolean> {

	public UnregisterAll() {
		setActionName(getClass().getSimpleName());
	}

	public void start() {
		List<String> registeredStudents = ((CoursePrivateState) state).getRegStudents();
		List<Action<?>> requiredActions = new ArrayList<>();
		for (String student : registeredStudents) {
			Action<Boolean> removeFromGradeSheet = new RemoveFromGradeSheet(actorId);
			requiredActions.add(removeFromGradeSheet);
			sendMessage(removeFromGradeSheet, student, new StudentPrivateState());
		}
		((CoursePrivateState) state).setAvailableSpots(-1);
		then(requiredActions, () -> {
			registeredStudents.clear();
			((CoursePrivateState) state).setRegistered(0);
			complete(true);
		});
	}
}
