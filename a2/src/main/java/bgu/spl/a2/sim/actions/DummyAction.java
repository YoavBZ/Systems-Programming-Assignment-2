package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

/**
 * This action simply delays the {@link Unregister} action, in order to prevent the issue -
 * where {@link ParticipateInCourse} action starts before {@link Unregister} and finishes after (if submitted in the same phase)
 */
public class DummyAction extends Action<Boolean> {

	public DummyAction() {
		setActionName(getClass().getSimpleName());
	}

	public void start() {
		complete(true);
	}
}
