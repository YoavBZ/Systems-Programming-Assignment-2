package bgu.spl.a2.sim.actions;

import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.Action;

public class RemoveFromGradeSheet extends Action<Boolean> {
	private String courseName;

	public RemoveFromGradeSheet(String courseName){
		this.courseName=courseName;
	}

	public void start(){
		System.out.println("#### " + getActionName() + ": start()");
		((StudentPrivateState)state).getGrades().remove(courseName);
		complete(true);
	}
}
