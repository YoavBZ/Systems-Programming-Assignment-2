package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class UnregisterAll extends Action<Boolean> {

	public UnregisterAll() {
		setActionName(getClass().getSimpleName());
	}

	public void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		List<String> registered = ((CoursePrivateState) state).getRegStudents();
		List<Action<?>> requiredActions = new ArrayList<>();
		for (String student : registered) {
			Action<Boolean> removeFromGradeSheet = new RemoveFromGradeSheet(actorId);
			requiredActions.add(removeFromGradeSheet);
			sendMessage(removeFromGradeSheet, student, new StudentPrivateState());
		}
		then(requiredActions, () -> {
			registered.clear();
			((CoursePrivateState) state).setAvailableSpots(-1);
			((CoursePrivateState) state).setRegistered(0);
			complete(true);
		});
	}
}
