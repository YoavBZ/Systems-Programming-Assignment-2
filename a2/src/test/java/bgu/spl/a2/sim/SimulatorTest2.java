package bgu.spl.a2.sim;

import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

import static junit.framework.TestCase.assertTrue;

@RunWith(Parameterized.class)
public class SimulatorTest2 {

	@Parameterized.Parameters
	public static Collection parameters() {
		return Arrays.asList(new Object[1000][]);
	}

	public SimulatorTest2(Object param) {
	}

	@Test
	public void main() {
		Simulator.main(new String[]{"input.json"});
		try (InputStream fin = new FileInputStream("result.ser");
		     ObjectInputStream ois = new ObjectInputStream(fin)) {
			HashMap<String, PrivateState> data = (HashMap<String, PrivateState>) ois.readObject();
			data.forEach((actor, state) -> {
				System.out.println(actor + ": ");
				System.out.print("History: ");
				state.getLogger().forEach((String s) -> System.out.print(s + ", "));
				System.out.println("");
				if (state instanceof DepartmentPrivateState) {
					System.out.print("Courses: ");
					((DepartmentPrivateState) state).getCourseList().forEach((String s) -> System.out.print(s + ", "));
					System.out.print('\n' + "Students: ");
					((DepartmentPrivateState) state).getStudentList().forEach((String s) -> System.out.print(s + ", "));
					System.out.println("");
					if (actor.equals("CS")) {
						assertTrue("Department" + actor + ": should have 3 open course action in logger", Collections.frequency(state.getLogger(), "Open Course") == 3);
						assertTrue("Department" + actor + ": should have 2 Add student action in logger", Collections.frequency(state.getLogger(), "Add Student") == 2);
						assertTrue("Department" + actor + ": should have 1 Administrative Check in logger", Collections.frequency(state.getLogger(), "Administrative Check") == 1);
						assertTrue("Department" + actor + ": not all courses appear in courselist " +
										" should have: Intro To CS, Data Structures, SPL",
								((DepartmentPrivateState) state).getCourseList().containsAll(Arrays.asList("Intro To CS", "Data Structures", "SPL")));
						assertTrue("Department" + actor + ": not all students appear in studentslist " +
										" should have: 123456789,5959595959",
								((DepartmentPrivateState) state).getStudentList().containsAll(Arrays.asList("123456789", "5959595959")));
					} else if (actor.equals("Math")) {
						assertTrue("Department" + actor + ": should have 1 Add student action in logger", Collections.frequency(state.getLogger(), "Add Student") == 1);
						assertTrue("Department" + actor + ": courselist should be empty",
								((DepartmentPrivateState) state).getCourseList().isEmpty());
						assertTrue("Department" + actor + ": not all students appear in studentslist " +
										" should have: 132424353",
								((DepartmentPrivateState) state).getStudentList().containsAll(Collections.singletonList("132424353")));
					}
				} else if (state instanceof StudentPrivateState) {
					System.out.print("Grades: ");
					((StudentPrivateState) state).getGrades().forEach((String s, Integer grade) -> System.out.print(s + ": " + grade + ", "));
					System.out.print('\n' + "Signature: ");
					System.out.println(((StudentPrivateState) state).getSignature());
					switch (actor) {
						case "123456789":
							assertTrue("student " + actor + ": should be registered to Intro To CS with grade 77", ((StudentPrivateState) state).getGrades().containsKey("Intro To CS") && ((StudentPrivateState) state).getGrades().get("Intro To CS") == 77);
							break;
						case "5959595959":
							assertTrue("student " + actor + ": should be registered to Intro To CS with grade 94", ((StudentPrivateState) state).getGrades().containsKey("Intro To CS") && ((StudentPrivateState) state).getGrades().get("Intro To CS") == 94);
							break;
						case "132424353":
							assertTrue("student " + actor + ": grades should be empty", ((StudentPrivateState) state).getGrades().isEmpty());
							assertTrue("student " + actor + ": signature should be 0", ((StudentPrivateState) state).getSignature() == 0);
							break;
					}
				} else {
					System.out.print("prerequisites: ");
					((CoursePrivateState) state).getPrequisites().forEach((String s) -> System.out.print(s + ", "));
					System.out.print('\n' + "students: ");
					((CoursePrivateState) state).getRegStudents().forEach((String s) -> System.out.print(s + ", "));
					System.out.print('\n' + "Registered: ");
					System.out.println(((CoursePrivateState) state).getRegistered());
					System.out.print("available spaces: ");
					System.out.println(((CoursePrivateState) state).getAvailableSpots());
					switch (actor) {
						case "Intro To CS":
							assertTrue("Course " + actor + ": should have 2 Participate In Course action in logger", Collections.frequency(state.getLogger(), "Participate In Course") == 2);
							assertTrue("Course " + actor + ": availableSpots should be 198", ((CoursePrivateState) state).getAvailableSpots() == 198);
							assertTrue("Course " + actor + ": should have 2 registered students", ((CoursePrivateState) state).getRegistered() == 2);
							assertTrue("Course " + actor + ": not all students appear in regStudents list " +
									" should have: 123456789, 5959595959", ((CoursePrivateState) state).getRegStudents().containsAll(new ArrayList<>(Arrays.asList("123456789", "5959595959"))));
							assertTrue("Course " + actor + ": should have no prerequisites", ((CoursePrivateState) state).getPrequisites().isEmpty());
							break;
						case "SPL":
							assertTrue("Course " + actor + ": should have 2 Participate In Course action in logger", Collections.frequency(state.getLogger(), "Participate In Course") == 2);
							assertTrue("Course " + actor + ": availableSpots should be 0", ((CoursePrivateState) state).getAvailableSpots() == 0);
							assertTrue("Course " + actor + ": should have 1 registered students", ((CoursePrivateState) state).getRegistered() == 1);
							assertTrue("Course" + actor + ": not all prerequisites appear, should have Intro To CS", ((CoursePrivateState) state).getPrequisites().contains("Intro To CS"));
							break;
						case "Data Structures":
							assertTrue("Course " + actor + ": should have 1 Participate In Course action and" +
									" 1 Unregister in logger", state.getLogger().containsAll(Arrays.asList("Participate In Course", "Unregister")));
							assertTrue("Course" + actor + ": not all prerequisites appear, should have Intro To CS", ((CoursePrivateState) state).getPrequisites().contains("Intro To CS"));
							break;
					}
				}
				System.out.println("----------------");
			});
			System.out.println(data.keySet());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}