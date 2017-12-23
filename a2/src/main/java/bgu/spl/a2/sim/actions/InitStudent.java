package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

public class InitStudent extends Action<Boolean> {

	public InitStudent() {
		setActionName(getClass().getSimpleName());
	}

	@Override
	protected void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		System.out.println("Initiated course " + actorId);
		complete(true);
	}
}