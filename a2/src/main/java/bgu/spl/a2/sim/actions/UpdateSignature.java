package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class UpdateSignature extends Action<Boolean> {

	private long signature;

	public UpdateSignature(long signature) {
		this.signature = signature;
		setActionName(getClass().getSimpleName());
	}

	@Override
	protected void start() {
		((StudentPrivateState) state).setSignature(signature);
		complete(true);
	}
}