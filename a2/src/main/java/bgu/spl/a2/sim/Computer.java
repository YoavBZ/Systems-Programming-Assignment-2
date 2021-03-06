package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

	public String computerType;
	long failSig;
	long successSig;

	public Computer(String computerType) {
		this.computerType = computerType;
	}

	/**
	 * this method checks if the courses' grades fulfill the conditions
	 *
	 * @param courses       courses that should be pass
	 * @param coursesGrades courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades) {
		for (String course : courses) {
			if (coursesGrades.containsKey(course)) {
				if (coursesGrades.get(course) < 56)
					return failSig;
			} else
				return failSig;
		}
		return successSig;
	}
}
