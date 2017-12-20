package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.*;

public class ParticipateInCourse extends Action<Boolean> {

	private String studentName;
	private int studentGrade;

	public ParticipateInCourse(String studentName, String studentGrade) {
		setActionName("Participate In Course");
		this.studentName = studentName;
		this.studentGrade = studentGrade.equals("-") ? -1 : Integer.valueOf(studentGrade);
	}

	@Override
	protected void start() {
		System.out.println("#### " + actorId + ": " + getActionName() + ": start()");
		state.addRecord(getActionName());
		List<String> regStudents = ((CoursePrivateState) state).getRegStudents();
		if (regStudents.contains(studentName) || ((CoursePrivateState) state).getAvailableSpots() == 0) {
			System.out.println("Student " + studentName + " is already registered, or there's no place");
			complete(false);
		} else {
			List<String> prequisites = ((CoursePrivateState) state).getPrequisites();
			Action<HashMap<String, Integer>> getCourses = new GetStudentGrades();
			List<Action<?>> requiredActions = new ArrayList<>();
			requiredActions.add(getCourses);
			sendMessage(getCourses, studentName, new StudentPrivateState());
			then(requiredActions, () -> {
				Set<String> result = getCourses.getResult().get().keySet();
				if (((CoursePrivateState) state).getAvailableSpots() > 0 && result.containsAll(prequisites)) {
					System.out.println("Registered " + studentName + " successfully");
					((CoursePrivateState) state).incRegistered();
					((CoursePrivateState) state).decAvailable();
					regStudents.add(studentName);
					Action<Boolean> addToGradeSheet = new AddToGradeSheet(actorId, studentGrade);
					requiredActions.add(addToGradeSheet);
					sendMessage(addToGradeSheet, studentName, new StudentPrivateState()).subscribe(() -> threadPool.submit(this, actorId, state));
					then(Collections.singleton(addToGradeSheet), () -> complete(true));
				} else {
					System.out.println("Registered " + studentName + " unsuccessfully");
					complete(false);
				}
			});
		}
	}
}
