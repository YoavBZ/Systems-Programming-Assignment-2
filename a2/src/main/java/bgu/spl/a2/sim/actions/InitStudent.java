package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class InitStudent extends Action<Boolean> {

	private String studentName;
	private long signature;

	public InitStudent(String studentName, long signature) {
		this.studentName = studentName;
		this.signature = signature;
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		((StudentPrivateState) state).setSignature(signature);
		System.out.println("Initiated student " + studentName);
		complete(true);
	}
}