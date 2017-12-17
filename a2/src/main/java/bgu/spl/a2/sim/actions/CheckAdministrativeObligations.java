package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.SuspendingMutex;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
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
	}

	@Override
	protected void start() {
		System.out.println("#### " + getActionName() + ": start()");
		List<Action<HashMap<String, Integer>>> requiredActions = new ArrayList<>();
		for (String student : students) {
			Action<HashMap<String, Integer>> getCourses = new GetStudentGrades();
			requiredActions.add(getCourses);
			sendMessage(getCourses, student, new StudentPrivateState());
		}
		then(requiredActions, () -> {
			for (SuspendingMutex suspendingMutex : warehouse.suspendingMutexes) {
				if (suspendingMutex.computer.computerType.equals(computerType)) {
					Promise<Computer> computerPromise = suspendingMutex.down();
					computerPromise.subscribe(() -> {
						for (int i = 0; i < requiredActions.size(); i++) {
							Action<HashMap<String, Integer>> action = requiredActions.get(i);
							HashMap<String, Integer> coursesGrades = action.getResult().get();
							long sig = computerPromise.get().checkAndSign(conditions, coursesGrades);
							Action<?> updateSignature = new UpdateSignature(students.get(i), sig);
							sendMessage(updateSignature, students.get(i), new StudentPrivateState());
							// TODO: call then() for completion?
						}
						suspendingMutex.up();
					});
				}
			}
		});
	}
}
