package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckAndSign extends Action<Boolean> {

	private Promise<Computer> computerPromise;
	private List<Action<HashMap<String, Integer>>> requiredActions;
	private List<String> students;
	private List<String> conditions;
	private Warehouse warehouse;

	public CheckAndSign(Promise<Computer> computerPromise, List<Action<HashMap<String, Integer>>> requiredActions, List<String> students, List<String> conditions, Warehouse warehouse) {
		this.computerPromise = computerPromise;
		this.requiredActions = requiredActions;
		this.students = students;
		this.conditions = conditions;
		this.warehouse = warehouse;
		setActionName(getClass().getSimpleName());
	}

	public void start() {
		List<Action<?>> updateSignatures = new ArrayList<>();
		for (int i = 0; i < requiredActions.size(); i++) {
			Action<HashMap<String, Integer>> action = requiredActions.get(i);
			HashMap<String, Integer> coursesGrades = action.getResult().get();
			long sig = computerPromise.get().checkAndSign(conditions, coursesGrades);
			Action<?> updateSignature = new UpdateSignature(sig);
			updateSignatures.add(updateSignature);
			sendMessage(updateSignature, students.get(i), new StudentPrivateState());
		}
		warehouse.computers.get(computerPromise.get().computerType).up();
		then(updateSignatures, () -> complete(true));
	}
}