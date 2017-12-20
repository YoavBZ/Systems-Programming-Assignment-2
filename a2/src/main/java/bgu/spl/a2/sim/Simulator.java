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
	private static File jsonFile;
	private static JsonObject jsonObject;
	private static Warehouse warehouse;

	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 */
	public static void start() {
		actorThreadPool.start();
		Computer[] computers = new Computer[jsonObject.get("Computers").getAsJsonArray().size()];
		int i = 0;
		for (JsonElement element : jsonObject.get("Computers").getAsJsonArray()) {
			computers[i] = new Computer(element.getAsJsonObject().get("Type").getAsString());
			computers[i].failSig = element.getAsJsonObject().get("Sig Fail").getAsLong();
			computers[i].successSig = element.getAsJsonObject().get("Sig Success").getAsLong();
			i++;
		}
		warehouse = new Warehouse(computers);
		// Phase 1
		runPhaseActions(jsonObject.get("Phase 1").getAsJsonArray());
		// Phase 2
		runPhaseActions(jsonObject.get("Phase 2").getAsJsonArray());
		// Phase 3
		runPhaseActions(jsonObject.get("Phase 3").getAsJsonArray());
	}

	private static ArrayList<String> toList(JsonArray arr) {
		ArrayList<String> list = new ArrayList<>();
		for (JsonElement element : arr) {
			list.add(element.getAsString());
		}
		return list;
	}

	private static void runPhaseActions(JsonArray array) {
		CountDownLatch countDownLatch = new CountDownLatch(array.size());
		for (JsonElement element : array) {
			JsonObject jsonObject = element.getAsJsonObject();
			Action<Boolean> action = null;
			if (jsonObject.get("Action").getAsString().equals("Participate In Course")) {
				action = new ParticipateInCourse(jsonObject.get("Student").getAsString(), jsonObject.get("Grade").getAsString());
				actorThreadPool.submit(action, jsonObject.get("Course").getAsString(), new CoursePrivateState());
			} else if (jsonObject.get("Action").getAsString().equals("Add Student")) {
				action = new AddNewStudent(jsonObject.get("Student").getAsString());
				actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
			} else if (jsonObject.get("Action").getAsString().equals("Open Course")) {
				action = new OpenNewCourse(jsonObject.get("Course").getAsString(), jsonObject.get("Space").getAsInt(), toList(jsonObject.get("Prerequisites").getAsJsonArray()), jsonObject.get("Department").getAsString());
				actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
			} else if (jsonObject.get("Action").getAsString().equals("Add Spaces")) {
				action = new OpenPlaceInCourse(jsonObject.get("Number").getAsInt());
				actorThreadPool.submit(action, jsonObject.get("Course").getAsString(), new CoursePrivateState());
			} else if (jsonObject.get("Action").getAsString().equals("Unregister")) {
				action = new Unregister(jsonObject.get("Student").getAsString());
				actorThreadPool.submit(action, jsonObject.get("Course").getAsString(), new CoursePrivateState());
			} else if (jsonObject.get("Action").getAsString().equals("Close Course")) {
				action = new CloseCourse(jsonObject.get("Course").getAsString());
				actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
			} else if (jsonObject.get("Action").getAsString().equals("Register With Preferences")) {
				action = new RegisterWithPreferences(toList(jsonObject.get("Preferences").getAsJsonArray()), toList(jsonObject.get("Grade").getAsJsonArray()));
				actorThreadPool.submit(action, jsonObject.get("Student").getAsString(), new StudentPrivateState());
			} else if (jsonObject.get("Action").getAsString().equals("Administrative Check")) {
				action = new CheckAdministrativeObligations(warehouse, jsonObject.get("Computer").getAsString(), toList(jsonObject.get("Students").getAsJsonArray()), toList(jsonObject.get("Conditions").getAsJsonArray()));
				actorThreadPool.submit(action, jsonObject.get("Department").getAsString(), new DepartmentPrivateState());
			}
			if (action != null) {
				action.getResult().subscribe(() -> {
					System.out.println("CountDown");
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
			actorThreadPool.shutdown();
		} catch (InterruptedException e) {
		}
		return (HashMap<String, PrivateState>) actorThreadPool.getActors();
	}

	public static void main(String[] args) {
		jsonFile = new File(args[0]);
		JsonParser parser = new JsonParser();
		try {
			JsonElement element = parser.parse(new FileReader(jsonFile));
			jsonObject = element.getAsJsonObject();
			attachActorThreadPool(new ActorThreadPool(jsonObject.get("threads").getAsInt()));
			start();
			HashMap<String, PrivateState> SimulationResult;
			SimulationResult = end();
			FileOutputStream fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(SimulationResult);
		} catch (Exception e) {
			e.printStackTrace();
//            return 1;
		}
//        return 0;
	}
}
