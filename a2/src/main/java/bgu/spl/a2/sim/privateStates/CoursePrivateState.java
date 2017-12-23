package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState {

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;

	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		registered = 0;
		availableSpots = 0;
		regStudents = new ArrayList<>();
		prequisites = new ArrayList<>();
	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public void setAvailableSpots(Integer availableSpots) {
		this.availableSpots = availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}

	public void setPrequisites(List<String> prequisites) {
		this.prequisites = prequisites;
	}

	public void incRegistered() {
		registered++;
	}

	public void incAvailable() {
		availableSpots++;
	}

	public void decAvailable() {
		availableSpots--;
	}

	public void decRegistered() {
		registered--;
	}

	public void setRegistered(int registered) {
		this.registered = registered;
	}
}
