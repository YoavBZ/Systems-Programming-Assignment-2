package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This method tries to register the student (= actorId) to (at most) one of the given preferences.
 */
public class RegisterWithPreferences extends Action<Boolean> {

	private ArrayList<String> preferences;
	private ArrayList<String> grades;

	public RegisterWithPreferences(ArrayList<String> preferences, ArrayList<String> grades) {
		setActionName("Register With Preferences");
		this.preferences = preferences;
		this.grades = grades;
	}

	public void start() {
		List<Action<Boolean>> requiredActions = new ArrayList<>();
		Action<Boolean> nextPreference = new ParticipateInCourse(actorId, grades.remove(0));
		requiredActions.add(nextPreference);
		for (int i = 1; i < preferences.size(); i++) {
			nextPreference = new ParticipateInCourse(actorId, grades.remove(0));
			requiredActions.add(nextPreference);
		}
		sendMessage(requiredActions.get(0), preferences.remove(0), new CoursePrivateState());
		then(Collections.singletonList(requiredActions.get(0)), () -> {
			if (requiredActions.remove(0).getResult().get()) {
				complete(true);
			} else if (requiredActions.isEmpty())
				// Failed to register the student to a course
				complete(false);
			else {
				// In case we can try to register the student to another course
				sendMessage(requiredActions.get(0), preferences.remove(0), new CoursePrivateState()).subscribe(() -> threadPool.submit(this, actorId, state));
			}
		});
	}
}
