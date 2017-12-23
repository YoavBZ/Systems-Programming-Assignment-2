package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.SuspendingMutex;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CheckAdministrativeObligations extends Action<Boolean> {

	private String computerType;
	private List<String> students;
	private List<String> conditions;
	private Warehouse warehouse;

	public CheckAdministrativeObligations(Warehouse warehouse, String computerType, ArrayList<String> students, ArrayList<String> conditions) {
		this.warehouse = warehouse;
		this.computerType = computerType;
		this.students = students;
		this.conditions = conditions;
		setActionName("Administrative Check");
	}

	@Override
	protected void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		List<Action<HashMap<String, Integer>>> requiredActions = new ArrayList<>();
		for (String student : students) {
			Action<HashMap<String, Integer>> getCourses = new GetStudentGrades();
			requiredActions.add(getCourses);
			sendMessage(getCourses, student, new StudentPrivateState());
		}
		SuspendingMutex suspendingMutex = warehouse.computers.get(computerType);
		then(requiredActions, () -> {
			Promise<Computer> computerPromise = suspendingMutex.down();
			Action<Boolean> checkAndSign = new CheckAndSign(computerPromise, requiredActions, students, conditions, warehouse);
			computerPromise.subscribe(() -> sendMessage(checkAndSign, actorId, state));
			then(Collections.singleton(checkAndSign), () -> {
				if (checkAndSign.getResult().get())
					complete(true);
				else
					complete(false);
			});
		});
	}
}