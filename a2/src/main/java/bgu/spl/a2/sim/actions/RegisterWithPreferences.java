package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegisterWithPreferences extends Action<Boolean> {

	private ArrayList<String> preferences;
	private ArrayList<String> grades;


	public RegisterWithPreferences(ArrayList<String> preferences, ArrayList<String> grades) {
		setActionName("Register With Preferences");
		this.preferences = preferences;
		this.grades = grades;
	}

	public void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		state.addRecord(getActionName());
		List<Action<Boolean>> requiredActions = new ArrayList<>();
		Action<Boolean> nextPreference = new ParticipateInCourse(actorId, grades.remove(0));
		requiredActions.add(nextPreference);
		for (int i = 1; i < preferences.size(); i++) {
			nextPreference = new ParticipateInCourse(actorId, grades.remove(0));
			nextPreference.getResult().subscribe(() -> threadPool.submit(this, actorId, state));
			requiredActions.add(nextPreference);
		}
		sendMessage(requiredActions.get(0), preferences.remove(0), new CoursePrivateState());
		then(Collections.singleton(requiredActions.get(0)), () -> {
			if (requiredActions.remove(0).getResult().get()) {
				complete(true);
			} else if (requiredActions.isEmpty())
				complete(false);
			else {
				sendMessage(requiredActions.get(0), preferences.remove(0), new CoursePrivateState());
			}
		});
	}

}
