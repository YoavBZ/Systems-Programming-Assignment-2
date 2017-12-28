/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	public static ActorThreadPool actorThreadPool;
	private static JsonObject jsonObject;
	private static Warehouse warehouse;

	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 * <p>
	 * The method starts the actorThreadPool, parses the jsonObject,
	 * constructs warehouse and run the phases, one at the time
	 */
	public static void start() {
		actorThreadPool.start();
		Computer[] computers = new Computer[jsonObject.get("Computers").getAsJsonArray().size()];
		for (int i = 0; i < jsonObject.get("Computers").getAsJsonArray().size(); i++) {
			JsonElement element = jsonObject.get("Computers").getAsJsonArray().get(i);
			computers[i] = new Computer(element.getAsJsonObject().get("Type").getAsString());
			computers[i].failSig = element.getAsJsonObject().get("Sig Fail").getAsLong();
			computers[i].successSig = element.getAsJsonObject().get("Sig Success").getAsLong();
		}
		warehouse = new Warehouse(computers);
		// Phase 1
		runPhaseActions(jsonObject.get("Phase 1").getAsJsonArray());
		// Phase 2
		runPhaseActions(jsonObject.get("Phase 2").getAsJsonArray());
		// Phase 3
		runPhaseActions(jsonObject.get("Phase 3").getAsJsonArray());
	}

	/**
	 * @param arr Gson array of strings
	 * @return an {@link ArrayList<String>} representing the array
	 */
	private static ArrayList<String> toList(JsonArray arr) {
		ArrayList<String> list = new ArrayList<>();
		for (JsonElement element : arr)
			list.add(element.getAsString());
		return list;
	}

	/**
	 * The method iterates a given array and submits all its action objects
	 * The method uses a {@link CountDownLatch} in order to block the main thread until all actions complete
	 *
	 * @param array Gson array of actions
	 */
	private static void runPhaseActions(JsonArray array) {
		CountDownLatch countDownLatch = new CountDownLatch(array.size());
		for (JsonElement element : array) {
			JsonObject jsonObject = element.getAsJsonObject();
			Action<Boolean> action = null;
			switch (jsonObject.get("Action").getAsString()) {
				case "Participate In Course":
					action = new ParticipateInCourse(jsonObject.get("Student").getAsString(), jsonObject.get("Grade").getAsString());
					actorThreadPool.submit(action, jsonObject.get("Course").getAsString(), new CoursePrivateState());
					break;
				case "Add Student":
					action = new AddStudent(jsonObject.get("Student").getAsString());
					actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
					break;
				case "Open Course":
					action = new OpenCourse(jsonObject.get("Course").getAsString(), jsonObject.get("Space").getAsInt(), toList(jsonObject.get("Prerequisites").getAsJsonArray()));
					actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
					break;
				case "Add Spaces":
					action = new OpenPlaceInCourse(jsonObject.get("Number").getAsInt());
					actorThreadPool.submit(action, jsonObject.get("Course").getAsString(), new CoursePrivateState());
					break;
				case "Unregister":
					action = new Unregister(jsonObject.get("Student").getAsString());
					actorThreadPool.submit(action, jsonObject.get("Course").getAsString(), new CoursePrivateState());
					break;
				case "Close Course":
					action = new CloseCourse(jsonObject.get("Course").getAsString());
					actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
					break;
				case "Register With Preferences":
					action = new RegisterWithPreferences(toList(jsonObject.get("Preferences").getAsJsonArray()), toList(jsonObject.get("Grade").getAsJsonArray()));
					actorThreadPool.submit(action, jsonObject.get("Student").getAsString(), new StudentPrivateState());
					break;
				case "Administrative Check":
					action = new CheckAdministrativeObligations(warehouse, jsonObject.get("Computer").getAsString(), toList(jsonObject.get("Students").getAsJsonArray()), toList(jsonObject.get("Conditions").getAsJsonArray()));
					actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
					break;
			}
			if (action != null) {
				action.getResult().subscribe(() -> {
					countDownLatch.countDown();
				});
			}
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	 *
	 * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	 */
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool) {
		actorThreadPool = myActorThreadPool;
	}

	/**
	 * shut down the simulation
	 * returns list of private states
	 */
	public static HashMap<String, PrivateState> end() {
		try {
			System.out.println("Shutting down Simulator");
			actorThreadPool.shutdown();
		} catch (InterruptedException ignored) {
		}
		return new HashMap<>(actorThreadPool.getActors());
	}

	/**
	 * @param args program's arguments
	 * @return 0 if no unhandled exceptions were thrown, or 1 otherwise
	 */
	public static int main(String[] args) {
		File jsonFile = new File(args[0]);
		JsonParser parser = new JsonParser();
		try {
			// Parsing the input file into a JsonObject
			jsonObject = parser.parse(new FileReader(jsonFile)).getAsJsonObject();
			// Initiating the ActorThreadPool with the given # of Thread
			attachActorThreadPool(new ActorThreadPool(jsonObject.get("threads").getAsInt()));
			start();
			HashMap<String, PrivateState> SimulationResult;
			SimulationResult = end();
			// Saving the result into an output file
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("result.ser"));
			oos.writeObject(SimulationResult);
		} catch (Exception e) {
			return 1;
		}
		return 0;
	}
}
